// $ANTLR 2.7.7 (2006-11-01): "cppparser.g" -> "CPPParser.java"$

/*REMOVE_BEGIN*/
package com.google.test.metric.cpp;
/*REMOVE_END*/

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class CPPParser extends antlr.LLkParser       implements STDCTokenTypes
 {


  private Builder b;

  String enclosingClass="";//name of current class
  boolean _td=false; // is type declaration?
  Hashtable symbols=new Hashtable();


  public boolean qualifiedItemIsOneOf(java.util.BitSet  qiFlags, int lookahead_offset) throws TokenStreamException
  {
    java.util.BitSet qi = qualifiedItemIs(lookahead_offset);
    java.util.BitSet aux=(java.util.BitSet) qi.clone();
    aux.and(qiFlags);
    return (!aux.isEmpty());
  }


  // This is an important function, but will be replaced with
  // an enhanced predicate in the future, once predicates
  // and/or predicate guards can contain loops.
  //
  // Scan past the ::T::B:: to see what lies beyond.
  // Return QI_TYPE if the qualified item can pose as type name.
  // Note that T::T is NOT a type; it is a constructor.  Also,
  // class T { ... T...} yields the enclosed T as a ctor.  This
  // is probably a type as I separate out the constructor defs/decls,
  // I want it consistent with T::T.
  //
  // In the below examples, I use A,B,T and example types, and
  // a,b as example ids.
  // In the below examples, any A or B may be a
  // qualified template, i.e.,  A<...>
  //
  // T::T outside of class T yields QI_CTOR.
  // T<...>::T outside of class T yields QI_CTOR.
  // T inside of class T {...} yields QI_CTOR.
  // T, ::T, A::T outside of class T yields QI_TYPE.
  // a, ::a,  A::B::a yields qiId
  // a::b yields QI_INVALID
  // ::operator, operator, A::B::operator yield qiOPerator
  // A::*, A::B::* yield QI_PTR_MEMBER
  // ::*, * yields QI_INVALID
  // ::~T, ~T, A::~T yield QI_DTOR
  // ~a, ~A::a, A::~T::, ~T:: yield QI_INVALID
  public java.util.BitSet qualifiedItemIs(int lookahead_offset) throws TokenStreamException
  {
    int value;
    int k = lookahead_offset + 1;
    int final_type_idx = 0;
    boolean scope_found = false;
    // Skip leading "::"
    if (LT(k).getType() == SCOPE)
    {
      k++;
      scope_found = true;
    }
    // Skip sequences of T:: or T<...>::
    //printf("support.cpp qualifiedItemIs while reached k %d type %d isType %d, isClass %d, guessing %d\n",
    //  k,LT(k)->getType(),isTypeName((LT(k)->getText()).data()),isClassName((LT(k)->getText()).data()),inputState->guessing);
    while ((LT(k).getType() == ID) && (isTypeName(LT(k).getText())))
    {// If this type is the same as the last type, then ctor
      if ((final_type_idx != 0) && (LT(final_type_idx).getText().equals(LT(k).getText())))
      {// Like T::T
      // As an extra check, do not allow T::T::
        if (LT(k+1).getType() == SCOPE)
        { //printf("support.cpp qualifiedItemIs QI_INVALID returned\n");
          return CPPvariables.QI_INVALID;
        }
        else
        {//printf("support.cpp qualifiedItemIs QI_CTOR returned\n");
          return CPPvariables.QI_CTOR;
        }
      }

      // Record this as the most recent type seen in the series
      final_type_idx = k;

      //printf("support.cpp qualifiedItemIs if step reached final_type_idx %d\n",final_type_idx);

      // Skip this token
      k++;

      // Skip over any template qualifiers <...>
      // I believe that "T<..." cannot be anything valid but a template
      if (LT(k).getType() == LESSTHAN)
      {
        value=skipTemplateQualifiers(k);
        if (value==k)
        {//printf("support.cpp qualifiedItemIs QI_INVALID(2) returned\n");
          return CPPvariables.QI_INVALID;
        }
        else
          k=value;
        //printf("support.cpp qualifiedItemIs template skipped, k %d\n",k);
        // k has been updated to token following <...>
      }
      if (LT(k).getType() == SCOPE)
      // Skip the "::" and keep going
      {
        k++;
        scope_found = true;
      }
      else
      {// Series terminated -- last ID in the sequence was a type
      // Return ctor if last type is in containing class
      // We already checked for T::T inside loop
        if ( enclosingClass.equals(LT(final_type_idx).getText()))
        { // Like class T  T()
          //printf("support.cpp qualifiedItemIs QI_CTOR(2) returned\n");
          return CPPvariables.QI_CTOR;
        }
        else
        {//printf("support.cpp qualifiedItemIs QI_TYPE returned\n");
          return CPPvariables.QI_TYPE;
        }
      }
    }
    // LT(k) is not an ID, or it is an ID but not a typename.
    //printf("support.cpp qualifiedItemIs second switch reached\n");
    switch (LT(k).getType())
    {
      case ID:
        // ID but not a typename
        // Do not allow id::
        if (LT(k+1).getType() == SCOPE)
        {
        //printf("support.cpp qualifiedItemIs QI_INVALID(3) returned\n");
          return CPPvariables.QI_INVALID;
        }
        if (enclosingClass.equals(LT(k).getText()))
        { // Like class T  T()
          //printf("support.cpp qualifiedItemIs QI_CTOR(3) returned\n");
          return CPPvariables.QI_CTOR;
        }
        else
        {
          if (scope_found)
            // DW 25/10/03 Assume type if at least one SCOPE present (test12.i)
            return CPPvariables.QI_TYPE;
                else
            //printf("support.cpp qualifiedItemIs QI_VAR returned\n");
            return CPPvariables.QI_VAR; // DW 19/03/04 was QI_ID Could be function?
        }
      case TILDE:
        // check for dtor
        if ((LT(k+1).getType() == ID) && (isTypeName(LT(k+1).getText())) &&(LT(k+2).getType() != SCOPE))
        { // Like ~B or A::B::~B
           // Also (incorrectly?) matches ::~A.
          //printf("support.cpp qualifiedItemIs QI_DTOR returned\n");
          return CPPvariables.QI_DTOR;
        }
        else
        { // ~a or ~A::a is QI_INVALID
          //printf("support.cpp qualifiedItemIs QI_INVALID(4) returned\n");
          return CPPvariables.QI_INVALID;
        }
      case STAR:
        // Like A::*
        // Do not allow * or ::*
        if (final_type_idx == 0)
        { // Haven't seen a type yet
          //printf("support.cpp qualifiedItemIs QI_INVALID(5) returned\n");
          return CPPvariables.QI_INVALID;
        }
        else
        { //printf("support.cpp qualifiedItemIs QI_PTR_MEMBER returned\n");
          return CPPvariables.QI_PTR_MEMBER;
        }
      case OPERATOR:
        // Like A::operator, ::operator, or operator
        //printf("support.cpp qualifiedItemIs QI_OPERATOR returned\n");
        return CPPvariables.QI_OPERATOR;
      default:
        // Something that neither starts with :: or ID, or
        // a :: not followed by ID, operator, ~, or *
        //printf("support.cpp qualifiedItemIs QI_INVALID(6) returned\n");
        return CPPvariables.QI_INVALID;
    }
  }

  // Skip over <...>.  This correctly handles nested <> and (), e.g:
  //    <T>
  //    < (i>3) >
  //    < T2<...> >
  // but not
  //    < i>3 >
  //
  // On input, kInOut is the index of the "<"
  // On output, if the return is true, then
  //                kInOut is the index of the token after ">"
  //            else
  //                kInOut is unchanged

  public int skipTemplateQualifiers(int kInOut)  throws TokenStreamException
  {
    // Start after "<"
    int k = kInOut + 1;
    int value;
    while (LT(k).getType() != GREATERTHAN) // scan to end of <...>
    {
      switch (LT(k).getType())
      {
        case EOF:
          return kInOut;
        case LESSTHAN:
            value=skipTemplateQualifiers(k);
          if (value==k)
          {
            return kInOut;
          }
          else
            k=value;
          break;
        case LPAREN:
          value=skipNestedParens(k);
          if (value==k)
          {
            return kInOut;
          }
          else
            k=value;
          break;
        default:
          k++;     // skip everything else
          break;
      }
      if (k > CPPvariables.MAX_TEMPLATE_TOKEN_SCAN)
      {
        return kInOut;
      }
    }

  // Update output argument to point past ">"
  kInOut = k + 1;
  return kInOut;
  }

  // Skip over (...).  This correctly handles nested (), e.g:
  //    (i>3, (i>5))
  //
  // On input, kInOut is the index of the "("
  // On output, if the return is true, then
  //                kInOut is the index of the token after ")"
  //            else
  //                kInOut is unchanged
  public int skipNestedParens(int kInOut)  throws TokenStreamException
  {
    // Start after "("
    int k = kInOut + 1;
    int value;
    while (LT(k).getType() != RPAREN)   // scan to end of (...)
    {
      switch (LT(k).getType())
      {
        case EOF:
          return kInOut;
        case LPAREN:
          value=skipNestedParens(k);
          if (value==k)
          {
            return kInOut;
          }
          else
            k=value;
          break;
        default:
          k++;     // skip everything else
          break;
      }
      if (k > CPPvariables.MAX_TEMPLATE_TOKEN_SCAN)
      {
        return kInOut;
      }
    }
    // Update output argument to point past ")"
    kInOut = k + 1;
    return kInOut;
  }

  // Return true if "::blah" or "fu::bar<args>::..." found.
  public boolean scopedItem(int k)  throws TokenStreamException
  {
    //printf("support.cpp scopedItem k %d\n",k);
    return (LT(k).getType()==SCOPE ||
      (LT(k).getType()==ID && !finalQualifier(k)));
  }

  // Return true if ID<...> or ID is last item in qualified item list.
  // Return false if LT(k) is not an ID.
  // ID must be a type to check for ID<...>,
  // or else we would get confused by "i<3"
  public boolean finalQualifier(int k)  throws TokenStreamException
  {
    if (LT(k).getType()==ID)
    {
      if ((isTypeName(LT(k).getText())) && (LT(k+1).getType()==LESSTHAN))
      {
        // Starts with "T<".  Skip <...>
        k++;
        k=skipTemplateQualifiers(k);
      }
      else
      {
        // skip ID;
        k++;
      }
      return (LT(k).getType() != SCOPE );
    }
    else
    { // not an ID
      return false;
    }
  }

  /*
   * Return true if 's' can pose as a type name
   */
  public boolean isTypeName(String s)
  {
    String type="";
    if (!symbols.containsKey(s))
    {
      //printf("support.cpp isTypeName %s not found\n",s);
      return false;
    }
    else
      type=(String) symbols.get(s);
    if (type.equals(CPPvariables.OT_TYPE_DEF)||
      type.equals(CPPvariables.OT_ENUM)||
      type.equals(CPPvariables.OT_CLASS)||
      type.equals(CPPvariables.OT_STRUCT)||
      type.equals(CPPvariables.OT_UNION))
    {
      return true;
    }
    return false;
  }

  public void declaratorID(String id, java.util.BitSet qi)
  {
    if ((qi.equals(CPPvariables.QI_TYPE)) || (_td)) // Check for type declaration
    {
      if (!symbols.containsKey(id))
        symbols.put(id, CPPvariables.OT_TYPE_DEF);
    }
  }

protected CPPParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public CPPParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected CPPParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public CPPParser(TokenStream lexer) {
  this(lexer,2);
}

public CPPParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void translation_unit(
		Builder builder
	) throws RecognitionException, TokenStreamException {
		
		
		if(!symbols.containsKey("std"))
		symbols.put("std",CPPvariables.OT_TYPE_DEF);
		b = builder;
		
		
		if ( inputState.guessing==0 ) {
			b.beginTranslationUnit();
		}
		{
		int _cnt3=0;
		_loop3:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				external_declaration();
			}
			else {
				if ( _cnt3>=1 ) { break _loop3; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt3++;
		} while (true);
		}
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			b.endTranslationUnit();
		}
	}
	
	public final void external_declaration() throws RecognitionException, TokenStreamException {
		
		String s="";
		
		{
		switch ( LA(1)) {
		case LITERAL_namespace:
		{
			decl_namespace();
			break;
		}
		case SEMICOLON:
		{
			match(SEMICOLON);
			break;
		}
		default:
			boolean synPredMatched7 = false;
			if (((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN))) {
				int _m7 = mark();
				synPredMatched7 = true;
				inputState.guessing++;
				try {
					{
					match(LITERAL_template);
					match(LESSTHAN);
					match(GREATERTHAN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched7 = false;
				}
				rewind(_m7);
inputState.guessing--;
			}
			if ( synPredMatched7 ) {
				match(LITERAL_template);
				match(LESSTHAN);
				match(GREATERTHAN);
				declaration();
			}
			else {
				boolean synPredMatched10 = false;
				if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
					int _m10 = mark();
					synPredMatched10 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case LITERAL_typedef:
						{
							match(LITERAL_typedef);
							break;
						}
						case LITERAL_class:
						case LITERAL_struct:
						case LITERAL_union:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						class_head();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched10 = false;
					}
					rewind(_m10);
inputState.guessing--;
				}
				if ( synPredMatched10 ) {
					declaration();
				}
				else {
					boolean synPredMatched12 = false;
					if (((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN))) {
						int _m12 = mark();
						synPredMatched12 = true;
						inputState.guessing++;
						try {
							{
							template_head();
							class_head();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched12 = false;
						}
						rewind(_m12);
inputState.guessing--;
					}
					if ( synPredMatched12 ) {
						template_head();
						declaration();
					}
					else {
						boolean synPredMatched15 = false;
						if (((LA(1)==LITERAL_enum) && (LA(2)==ID||LA(2)==LCURLY))) {
							int _m15 = mark();
							synPredMatched15 = true;
							inputState.guessing++;
							try {
								{
								match(LITERAL_enum);
								{
								switch ( LA(1)) {
								case ID:
								{
									match(ID);
									break;
								}
								case LCURLY:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								}
								}
								match(LCURLY);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched15 = false;
							}
							rewind(_m15);
inputState.guessing--;
						}
						if ( synPredMatched15 ) {
							enum_specifier();
							{
							switch ( LA(1)) {
							case ID:
							case LITERAL__stdcall:
							case LITERAL___stdcall:
							case LPAREN:
							case OPERATOR:
							case LITERAL_this:
							case LITERAL_true:
							case LITERAL_false:
							case STAR:
							case AMPERSAND:
							case TILDE:
							case SCOPE:
							case LITERAL__cdecl:
							case LITERAL___cdecl:
							case LITERAL__near:
							case LITERAL___near:
							case LITERAL__far:
							case LITERAL___far:
							case LITERAL___interrupt:
							case LITERAL_pascal:
							case LITERAL__pascal:
							case LITERAL___pascal:
							{
								init_declarator_list();
								break;
							}
							case SEMICOLON:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							match(SEMICOLON);
						}
						else {
							boolean synPredMatched19 = false;
							if (((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2))))) {
								int _m19 = mark();
								synPredMatched19 = true;
								inputState.guessing++;
								try {
									{
									{
									switch ( LA(1)) {
									case LITERAL_template:
									{
										template_head();
										break;
									}
									case ID:
									case LITERAL_inline:
									case LITERAL__inline:
									case LITERAL___inline:
									case LITERAL_virtual:
									case TILDE:
									case SCOPE:
									{
										break;
									}
									default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
									}
									}
									dtor_head();
									match(LCURLY);
									}
								}
								catch (RecognitionException pe) {
									synPredMatched19 = false;
								}
								rewind(_m19);
inputState.guessing--;
							}
							if ( synPredMatched19 ) {
								{
								switch ( LA(1)) {
								case LITERAL_template:
								{
									template_head();
									break;
								}
								case ID:
								case LITERAL_inline:
								case LITERAL__inline:
								case LITERAL___inline:
								case LITERAL_virtual:
								case TILDE:
								case SCOPE:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								}
								}
								dtor_head();
								dtor_body();
							}
							else {
								boolean synPredMatched23 = false;
								if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))))) {
									int _m23 = mark();
									synPredMatched23 = true;
									inputState.guessing++;
									try {
										{
										{
										if ((true) && (true)) {
											ctor_decl_spec();
										}
										else {
										}
										
										}
										if (!(qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)))
										  throw new SemanticException("qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)");
										}
									}
									catch (RecognitionException pe) {
										synPredMatched23 = false;
									}
									rewind(_m23);
inputState.guessing--;
								}
								if ( synPredMatched23 ) {
									ctor_definition();
								}
								else {
									boolean synPredMatched26 = false;
									if (((_tokenSet_7.member(LA(1))) && (_tokenSet_8.member(LA(2))))) {
										int _m26 = mark();
										synPredMatched26 = true;
										inputState.guessing++;
										try {
											{
											{
											switch ( LA(1)) {
											case LITERAL_inline:
											{
												match(LITERAL_inline);
												break;
											}
											case ID:
											case OPERATOR:
											case SCOPE:
											{
												break;
											}
											default:
											{
												throw new NoViableAltException(LT(1), getFilename());
											}
											}
											}
											scope_override();
											conversion_function_decl_or_def();
											}
										}
										catch (RecognitionException pe) {
											synPredMatched26 = false;
										}
										rewind(_m26);
inputState.guessing--;
									}
									if ( synPredMatched26 ) {
										{
										switch ( LA(1)) {
										case LITERAL_inline:
										{
											match(LITERAL_inline);
											break;
										}
										case ID:
										case OPERATOR:
										case SCOPE:
										{
											break;
										}
										default:
										{
											throw new NoViableAltException(LT(1), getFilename());
										}
										}
										}
										s=scope_override();
										conversion_function_decl_or_def();
									}
									else {
										boolean synPredMatched29 = false;
										if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
											int _m29 = mark();
											synPredMatched29 = true;
											inputState.guessing++;
											try {
												{
												declaration_specifiers();
												function_declarator();
												match(SEMICOLON);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched29 = false;
											}
											rewind(_m29);
inputState.guessing--;
										}
										if ( synPredMatched29 ) {
											declaration();
										}
										else {
											boolean synPredMatched31 = false;
											if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
												int _m31 = mark();
												synPredMatched31 = true;
												inputState.guessing++;
												try {
													{
													declaration_specifiers();
													function_declarator();
													match(LCURLY);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched31 = false;
												}
												rewind(_m31);
inputState.guessing--;
											}
											if ( synPredMatched31 ) {
												if ( inputState.guessing==0 ) {
													b.beginFunctionDefinition();
												}
												function_definition();
												if ( inputState.guessing==0 ) {
													b.endFunctionDefinition();
												}
											}
											else {
												boolean synPredMatched33 = false;
												if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
													int _m33 = mark();
													synPredMatched33 = true;
													inputState.guessing++;
													try {
														{
														declaration_specifiers();
														function_declarator();
														declaration();
														}
													}
													catch (RecognitionException pe) {
														synPredMatched33 = false;
													}
													rewind(_m33);
inputState.guessing--;
												}
												if ( synPredMatched33 ) {
													function_definition();
												}
												else {
													boolean synPredMatched36 = false;
													if (((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN))) {
														int _m36 = mark();
														synPredMatched36 = true;
														inputState.guessing++;
														try {
															{
															template_head();
															declaration_specifiers();
															{
															switch ( LA(1)) {
															case ID:
															case LITERAL__stdcall:
															case LITERAL___stdcall:
															case LPAREN:
															case OPERATOR:
															case LITERAL_this:
															case LITERAL_true:
															case LITERAL_false:
															case STAR:
															case AMPERSAND:
															case TILDE:
															case SCOPE:
															case LITERAL__cdecl:
															case LITERAL___cdecl:
															case LITERAL__near:
															case LITERAL___near:
															case LITERAL__far:
															case LITERAL___far:
															case LITERAL___interrupt:
															case LITERAL_pascal:
															case LITERAL__pascal:
															case LITERAL___pascal:
															{
																init_declarator_list();
																break;
															}
															case SEMICOLON:
															{
																break;
															}
															default:
															{
																throw new NoViableAltException(LT(1), getFilename());
															}
															}
															}
															match(SEMICOLON);
															}
														}
														catch (RecognitionException pe) {
															synPredMatched36 = false;
														}
														rewind(_m36);
inputState.guessing--;
													}
													if ( synPredMatched36 ) {
														template_head();
														declaration_specifiers();
														{
														switch ( LA(1)) {
														case ID:
														case LITERAL__stdcall:
														case LITERAL___stdcall:
														case LPAREN:
														case OPERATOR:
														case LITERAL_this:
														case LITERAL_true:
														case LITERAL_false:
														case STAR:
														case AMPERSAND:
														case TILDE:
														case SCOPE:
														case LITERAL__cdecl:
														case LITERAL___cdecl:
														case LITERAL__near:
														case LITERAL___near:
														case LITERAL__far:
														case LITERAL___far:
														case LITERAL___interrupt:
														case LITERAL_pascal:
														case LITERAL__pascal:
														case LITERAL___pascal:
														{
															init_declarator_list();
															break;
														}
														case SEMICOLON:
														{
															break;
														}
														default:
														{
															throw new NoViableAltException(LT(1), getFilename());
														}
														}
														}
														match(SEMICOLON);
													}
													else if ((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN)) {
														template_head();
														{
														boolean synPredMatched40 = false;
														if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))))) {
															int _m40 = mark();
															synPredMatched40 = true;
															inputState.guessing++;
															try {
																{
																ctor_decl_spec();
																if (!(qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)))
																  throw new SemanticException("qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)");
																}
															}
															catch (RecognitionException pe) {
																synPredMatched40 = false;
															}
															rewind(_m40);
inputState.guessing--;
														}
														if ( synPredMatched40 ) {
															ctor_definition();
														}
														else {
															boolean synPredMatched42 = false;
															if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
																int _m42 = mark();
																synPredMatched42 = true;
																inputState.guessing++;
																try {
																	{
																	declaration_specifiers();
																	function_declarator();
																	match(SEMICOLON);
																	}
																}
																catch (RecognitionException pe) {
																	synPredMatched42 = false;
																}
																rewind(_m42);
inputState.guessing--;
															}
															if ( synPredMatched42 ) {
																declaration();
															}
															else {
																boolean synPredMatched44 = false;
																if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
																	int _m44 = mark();
																	synPredMatched44 = true;
																	inputState.guessing++;
																	try {
																		{
																		declaration_specifiers();
																		function_declarator();
																		match(LCURLY);
																		}
																	}
																	catch (RecognitionException pe) {
																		synPredMatched44 = false;
																	}
																	rewind(_m44);
inputState.guessing--;
																}
																if ( synPredMatched44 ) {
																	function_definition();
																}
																else {
																	throw new NoViableAltException(LT(1), getFilename());
																}
																}}
																}
															}
															else if ((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2)))) {
																declaration();
															}
														else {
															throw new NoViableAltException(LT(1), getFilename());
														}
														}}}}}}}}}}}
														}
													}
													
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		
		boolean synPredMatched106 = false;
		if (((LA(1)==LITERAL_extern) && (LA(2)==StringLiteral))) {
			int _m106 = mark();
			synPredMatched106 = true;
			inputState.guessing++;
			try {
				{
				match(LITERAL_extern);
				match(StringLiteral);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched106 = false;
			}
			rewind(_m106);
inputState.guessing--;
		}
		if ( synPredMatched106 ) {
			linkage_specification();
		}
		else if ((_tokenSet_11.member(LA(1))) && (_tokenSet_12.member(LA(2)))) {
			declaration_specifiers();
			{
			switch ( LA(1)) {
			case ID:
			case COMMA:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LPAREN:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				{
				switch ( LA(1)) {
				case COMMA:
				{
					match(COMMA);
					break;
				}
				case ID:
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				case LPAREN:
				case OPERATOR:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_false:
				case STAR:
				case AMPERSAND:
				case TILDE:
				case SCOPE:
				case LITERAL__cdecl:
				case LITERAL___cdecl:
				case LITERAL__near:
				case LITERAL___near:
				case LITERAL__far:
				case LITERAL___far:
				case LITERAL___interrupt:
				case LITERAL_pascal:
				case LITERAL__pascal:
				case LITERAL___pascal:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				init_declarator_list();
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(SEMICOLON);
		}
		else if ((LA(1)==LITERAL_using)) {
			using_declaration();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void class_head() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case LITERAL_struct:
		{
			match(LITERAL_struct);
			break;
		}
		case LITERAL_union:
		{
			match(LITERAL_union);
			break;
		}
		case LITERAL_class:
		{
			match(LITERAL_class);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case ID:
		{
			match(ID);
			{
			switch ( LA(1)) {
			case LESSTHAN:
			{
				match(LESSTHAN);
				template_argument_list();
				match(GREATERTHAN);
				break;
			}
			case LCURLY:
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COLON:
			{
				base_clause();
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LCURLY);
	}
	
	public final void template_head() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_template);
		match(LESSTHAN);
		template_parameter_list();
		match(GREATERTHAN);
	}
	
	public final void enum_specifier() throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		
		match(LITERAL_enum);
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			match(LCURLY);
			enumerator_list();
			match(RCURLY);
			break;
		}
		case ID:
		{
			id = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				if(!symbols.containsKey(id.getText()))
				symbols.put(id.getText(),CPPvariables.OT_ENUM);
				
			}
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				match(LCURLY);
				enumerator_list();
				match(RCURLY);
				break;
			}
			case LESSTHAN:
			case GREATERTHAN:
			case ID:
			case SEMICOLON:
			case RCURLY:
			case ASSIGNEQUAL:
			case COLON:
			case COMMA:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LPAREN:
			case RPAREN:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case LSQUARE:
			case RSQUARE:
			case TILDE:
			case ELLIPSIS:
			case TIMESEQUAL:
			case DIVIDEEQUAL:
			case MINUSEQUAL:
			case PLUSEQUAL:
			case MODEQUAL:
			case SHIFTLEFTEQUAL:
			case SHIFTRIGHTEQUAL:
			case BITWISEANDEQUAL:
			case BITWISEXOREQUAL:
			case BITWISEOREQUAL:
			case QUESTIONMARK:
			case OR:
			case AND:
			case BITWISEOR:
			case BITWISEXOR:
			case NOTEQUAL:
			case EQUAL:
			case LESSTHANOREQUALTO:
			case GREATERTHANOREQUALTO:
			case SHIFTLEFT:
			case SHIFTRIGHT:
			case PLUS:
			case MINUS:
			case DIVIDE:
			case MOD:
			case DOTMBR:
			case POINTERTOMBR:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void init_declarator_list() throws RecognitionException, TokenStreamException {
		
		
		init_declarator();
		{
		_loop156:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				init_declarator();
			}
			else {
				break _loop156;
			}
			
		} while (true);
		}
	}
	
	public final void dtor_head() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			b.beginDtorHead();
		}
		dtor_decl_spec();
		dtor_declarator();
		if ( inputState.guessing==0 ) {
			b.endDtorHead();
		}
	}
	
	public final void dtor_body() throws RecognitionException, TokenStreamException {
		
		
		compound_statement();
	}
	
	public final void ctor_decl_spec() throws RecognitionException, TokenStreamException {
		
		List declSpecs = new ArrayList();
		
		{
		_loop238:
		do {
			switch ( LA(1)) {
			case LITERAL_inline:
			case LITERAL__inline:
			case LITERAL___inline:
			{
				{
				switch ( LA(1)) {
				case LITERAL_inline:
				{
					match(LITERAL_inline);
					break;
				}
				case LITERAL__inline:
				{
					match(LITERAL__inline);
					break;
				}
				case LITERAL___inline:
				{
					match(LITERAL___inline);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					declSpecs.add("inline");
				}
				break;
			}
			case LITERAL_explicit:
			{
				match(LITERAL_explicit);
				if ( inputState.guessing==0 ) {
					declSpecs.add("explicit");
				}
				break;
			}
			default:
			{
				break _loop238;
			}
			}
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			b.declarationSpecifiers(declSpecs);
		}
	}
	
	public final void ctor_definition() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			b.beginCtorDefinition();
		}
		ctor_head();
		ctor_body();
		if ( inputState.guessing==0 ) {
			b.endCtorDefinition();
		}
	}
	
	public final String  scope_override() throws RecognitionException, TokenStreamException {
		String s="";
		
		Token  id = null;
		
		String sitem="";
		
		
		{
		switch ( LA(1)) {
		case SCOPE:
		{
			match(SCOPE);
			if ( inputState.guessing==0 ) {
				sitem=sitem+"::";
			}
			break;
		}
		case ID:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case STAR:
		case TILDE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop455:
		do {
			if (((LA(1)==ID) && (LA(2)==LESSTHAN||LA(2)==SCOPE))&&(scopedItem(1))) {
				id = LT(1);
				match(ID);
				{
				switch ( LA(1)) {
				case LESSTHAN:
				{
					match(LESSTHAN);
					template_argument_list();
					match(GREATERTHAN);
					break;
				}
				case SCOPE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(SCOPE);
				if ( inputState.guessing==0 ) {
					
					sitem=sitem+id.getText();
					sitem=sitem+"::";
					
				}
			}
			else {
				break _loop455;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			s = sitem;
		}
		return s;
	}
	
	public final void conversion_function_decl_or_def() throws RecognitionException, TokenStreamException {
		
		
		match(OPERATOR);
		declaration_specifiers();
		{
		switch ( LA(1)) {
		case STAR:
		{
			match(STAR);
			break;
		}
		case AMPERSAND:
		{
			match(AMPERSAND);
			break;
		}
		case LESSTHAN:
		case LPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LESSTHAN:
		{
			match(LESSTHAN);
			template_parameter_list();
			match(GREATERTHAN);
			break;
		}
		case LPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case ELLIPSIS:
		case SCOPE:
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		case LITERAL__near:
		case LITERAL___near:
		case LITERAL__far:
		case LITERAL___far:
		case LITERAL___interrupt:
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			parameter_list();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		{
			type_qualifier();
			break;
		}
		case LCURLY:
		case SEMICOLON:
		case LITERAL_throw:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LITERAL_throw:
		{
			exception_specification();
			break;
		}
		case LCURLY:
		case SEMICOLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			compound_statement();
			break;
		}
		case SEMICOLON:
		{
			match(SEMICOLON);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void declaration_specifiers() throws RecognitionException, TokenStreamException {
		
		_td=false; boolean td=false; List declSpecs = new ArrayList();
		
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case SCOPE:
		{
			{
			_loop119:
			do {
				switch ( LA(1)) {
				case LITERAL_extern:
				case LITERAL_auto:
				case LITERAL_register:
				case LITERAL_static:
				case LITERAL_mutable:
				{
					storage_class_specifier();
					break;
				}
				case LITERAL_const:
				case LITERAL_const_cast:
				case LITERAL_volatile:
				{
					type_qualifier();
					break;
				}
				case LITERAL_inline:
				case LITERAL__inline:
				case LITERAL___inline:
				{
					{
					switch ( LA(1)) {
					case LITERAL_inline:
					{
						match(LITERAL_inline);
						break;
					}
					case LITERAL__inline:
					{
						match(LITERAL__inline);
						break;
					}
					case LITERAL___inline:
					{
						match(LITERAL___inline);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						declSpecs.add("inline");
					}
					break;
				}
				case LITERAL_virtual:
				{
					match(LITERAL_virtual);
					if ( inputState.guessing==0 ) {
						declSpecs.add("virtual");
					}
					break;
				}
				case LITERAL_explicit:
				{
					match(LITERAL_explicit);
					if ( inputState.guessing==0 ) {
						declSpecs.add("explicit");
					}
					break;
				}
				case LITERAL_typedef:
				{
					match(LITERAL_typedef);
					if ( inputState.guessing==0 ) {
						td=true; declSpecs.add("typedef");
					}
					break;
				}
				case LITERAL_friend:
				{
					match(LITERAL_friend);
					if ( inputState.guessing==0 ) {
						declSpecs.add("friend");
					}
					break;
				}
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				{
					{
					switch ( LA(1)) {
					case LITERAL__stdcall:
					{
						match(LITERAL__stdcall);
						break;
					}
					case LITERAL___stdcall:
					{
						match(LITERAL___stdcall);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						declSpecs.add("__stdcall");
					}
					break;
				}
				default:
					if ((LA(1)==LITERAL__declspec||LA(1)==LITERAL___declspec) && (LA(2)==LPAREN)) {
						{
						switch ( LA(1)) {
						case LITERAL__declspec:
						{
							match(LITERAL__declspec);
							break;
						}
						case LITERAL___declspec:
						{
							match(LITERAL___declspec);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(LPAREN);
						match(ID);
						match(RPAREN);
					}
				else {
					break _loop119;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				if (!declSpecs.isEmpty()) b.declarationSpecifiers(declSpecs);
			}
			type_specifier();
			break;
		}
		case LITERAL_typename:
		{
			match(LITERAL_typename);
			if ( inputState.guessing==0 ) {
				td=true;
			}
			direct_declarator();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			_td=td;
		}
	}
	
	public final void function_declarator() throws RecognitionException, TokenStreamException {
		
		
		boolean synPredMatched225 = false;
		if (((_tokenSet_13.member(LA(1))) && (_tokenSet_14.member(LA(2))))) {
			int _m225 = mark();
			synPredMatched225 = true;
			inputState.guessing++;
			try {
				{
				ptr_operator();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched225 = false;
			}
			rewind(_m225);
inputState.guessing--;
		}
		if ( synPredMatched225 ) {
			ptr_operator();
			function_declarator();
		}
		else if ((_tokenSet_15.member(LA(1))) && (_tokenSet_16.member(LA(2)))) {
			function_direct_declarator();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void function_definition() throws RecognitionException, TokenStreamException {
		
		java.util.BitSet auxBitSet=(java.util.BitSet)CPPvariables.QI_TYPE.clone(); auxBitSet.or(CPPvariables.QI_CTOR);
		
		{
		if (((_tokenSet_11.member(LA(1))) && (_tokenSet_17.member(LA(2))))&&(( !(LA(1)==SCOPE||LA(1)==ID) || qualifiedItemIsOneOf(auxBitSet,0) ))) {
			declaration_specifiers();
			function_declarator();
			{
			if ((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2)))) {
				{
				_loop100:
				do {
					if ((_tokenSet_1.member(LA(1)))) {
						declaration();
					}
					else {
						break _loop100;
					}
					
				} while (true);
				}
			}
			else if ((LA(1)==LCURLY) && (_tokenSet_20.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			compound_statement();
		}
		else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2)))) {
			function_declarator();
			{
			if ((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2)))) {
				{
				_loop103:
				do {
					if ((_tokenSet_1.member(LA(1)))) {
						declaration();
					}
					else {
						break _loop103;
					}
					
				} while (true);
				}
			}
			else if ((LA(1)==LCURLY) && (_tokenSet_20.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			compound_statement();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void decl_namespace() throws RecognitionException, TokenStreamException {
		
		Token  ns = null;
		Token  ns2 = null;
		String qid="";
		
		match(LITERAL_namespace);
		{
		if ((LA(1)==ID||LA(1)==LCURLY) && (_tokenSet_23.member(LA(2)))) {
			{
			switch ( LA(1)) {
			case ID:
			{
				ns = LT(1);
				match(ID);
				if ( inputState.guessing==0 ) {
					_td = true;declaratorID(ns.getText(),CPPvariables.QI_TYPE);
				}
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LCURLY);
			if ( inputState.guessing==0 ) {
				b.enterNamespaceScope(ns.getText());
			}
			{
			_loop49:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					external_declaration();
				}
				else {
					break _loop49;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				b.exitNamespaceScope();
			}
			match(RCURLY);
		}
		else if ((LA(1)==ID) && (LA(2)==ASSIGNEQUAL)) {
			ns2 = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				_td=true;declaratorID(ns2.getText(),CPPvariables.QI_TYPE);
			}
			match(ASSIGNEQUAL);
			qid=qualified_id();
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.makeNamespaceAlias(qid, ns2.getText());
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final String  qualified_id() throws RecognitionException, TokenStreamException {
		String q="";
		
		Token  id = null;
		
		String so="";
		String qitem="";
		
		
		so=scope_override();
		if ( inputState.guessing==0 ) {
			qitem=so;
		}
		{
		switch ( LA(1)) {
		case ID:
		{
			id = LT(1);
			match(ID);
			{
			if ((LA(1)==LESSTHAN) && (_tokenSet_24.member(LA(2)))) {
				match(LESSTHAN);
				template_argument_list();
				match(GREATERTHAN);
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			if ( inputState.guessing==0 ) {
				
				qitem=qitem+id.getText();
				
			}
			break;
		}
		case OPERATOR:
		{
			match(OPERATOR);
			optor();
			if ( inputState.guessing==0 ) {
				qitem=qitem+"operator"+"NYI";
			}
			break;
		}
		case LITERAL_this:
		{
			match(LITERAL_this);
			break;
		}
		case LITERAL_true:
		case LITERAL_false:
		{
			{
			switch ( LA(1)) {
			case LITERAL_true:
			{
				match(LITERAL_true);
				break;
			}
			case LITERAL_false:
			{
				match(LITERAL_false);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			q = qitem;
		}
		return q;
	}
	
	public final void member_declaration() throws RecognitionException, TokenStreamException {
		
		String q="";
		
		if ( inputState.guessing==0 ) {
			b.beginMemberDeclaration();
		}
		{
		switch ( LA(1)) {
		case LITERAL_public:
		case LITERAL_protected:
		case LITERAL_private:
		{
			access_specifier();
			match(COLON);
			break;
		}
		case SEMICOLON:
		{
			match(SEMICOLON);
			break;
		}
		default:
			boolean synPredMatched54 = false;
			if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
				int _m54 = mark();
				synPredMatched54 = true;
				inputState.guessing++;
				try {
					{
					{
					switch ( LA(1)) {
					case LITERAL_typedef:
					{
						match(LITERAL_typedef);
						break;
					}
					case LITERAL_class:
					case LITERAL_struct:
					case LITERAL_union:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					class_head();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched54 = false;
				}
				rewind(_m54);
inputState.guessing--;
			}
			if ( synPredMatched54 ) {
				declaration();
			}
			else {
				boolean synPredMatched57 = false;
				if (((LA(1)==LITERAL_enum) && (LA(2)==ID||LA(2)==LCURLY))) {
					int _m57 = mark();
					synPredMatched57 = true;
					inputState.guessing++;
					try {
						{
						match(LITERAL_enum);
						{
						switch ( LA(1)) {
						case ID:
						{
							match(ID);
							break;
						}
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(LCURLY);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched57 = false;
					}
					rewind(_m57);
inputState.guessing--;
				}
				if ( synPredMatched57 ) {
					enum_specifier();
					{
					switch ( LA(1)) {
					case ID:
					case COLON:
					case LITERAL__stdcall:
					case LITERAL___stdcall:
					case LPAREN:
					case OPERATOR:
					case LITERAL_this:
					case LITERAL_true:
					case LITERAL_false:
					case STAR:
					case AMPERSAND:
					case TILDE:
					case SCOPE:
					case LITERAL__cdecl:
					case LITERAL___cdecl:
					case LITERAL__near:
					case LITERAL___near:
					case LITERAL__far:
					case LITERAL___far:
					case LITERAL___interrupt:
					case LITERAL_pascal:
					case LITERAL__pascal:
					case LITERAL___pascal:
					{
						member_declarator_list();
						break;
					}
					case SEMICOLON:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(SEMICOLON);
				}
				else {
					boolean synPredMatched60 = false;
					if (((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN))) {
						int _m60 = mark();
						synPredMatched60 = true;
						inputState.guessing++;
						try {
							{
							template_head();
							class_head();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched60 = false;
						}
						rewind(_m60);
inputState.guessing--;
					}
					if ( synPredMatched60 ) {
						template_head();
						declaration();
					}
					else {
						boolean synPredMatched62 = false;
						if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))))) {
							int _m62 = mark();
							synPredMatched62 = true;
							inputState.guessing++;
							try {
								{
								ctor_decl_spec();
								if (!(qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)))
								  throw new SemanticException("qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)");
								ctor_declarator();
								match(SEMICOLON);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched62 = false;
							}
							rewind(_m62);
inputState.guessing--;
						}
						if ( synPredMatched62 ) {
							ctor_decl_spec();
							ctor_declarator();
							match(SEMICOLON);
						}
						else {
							boolean synPredMatched65 = false;
							if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))))) {
								int _m65 = mark();
								synPredMatched65 = true;
								inputState.guessing++;
								try {
									{
									ctor_decl_spec();
									if (!(qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)))
									  throw new SemanticException("qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)");
									ctor_declarator();
									{
									switch ( LA(1)) {
									case COLON:
									{
										match(COLON);
										break;
									}
									case LCURLY:
									{
										match(LCURLY);
										break;
									}
									default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
									}
									}
									}
								}
								catch (RecognitionException pe) {
									synPredMatched65 = false;
								}
								rewind(_m65);
inputState.guessing--;
							}
							if ( synPredMatched65 ) {
								ctor_definition();
							}
							else {
								boolean synPredMatched67 = false;
								if (((_tokenSet_27.member(LA(1))) && (_tokenSet_4.member(LA(2))))) {
									int _m67 = mark();
									synPredMatched67 = true;
									inputState.guessing++;
									try {
										{
										dtor_head();
										match(SEMICOLON);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched67 = false;
									}
									rewind(_m67);
inputState.guessing--;
								}
								if ( synPredMatched67 ) {
									dtor_head();
									match(SEMICOLON);
								}
								else {
									boolean synPredMatched69 = false;
									if (((_tokenSet_27.member(LA(1))) && (_tokenSet_4.member(LA(2))))) {
										int _m69 = mark();
										synPredMatched69 = true;
										inputState.guessing++;
										try {
											{
											dtor_head();
											match(LCURLY);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched69 = false;
										}
										rewind(_m69);
inputState.guessing--;
									}
									if ( synPredMatched69 ) {
										dtor_head();
										dtor_body();
									}
									else {
										boolean synPredMatched71 = false;
										if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
											int _m71 = mark();
											synPredMatched71 = true;
											inputState.guessing++;
											try {
												{
												declaration_specifiers();
												function_declarator();
												match(SEMICOLON);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched71 = false;
											}
											rewind(_m71);
inputState.guessing--;
										}
										if ( synPredMatched71 ) {
											if ( inputState.guessing==0 ) {
												b.beginFunctionDeclaration();
											}
											declaration();
											if ( inputState.guessing==0 ) {
												b.endFunctionDeclaration();
											}
										}
										else {
											boolean synPredMatched73 = false;
											if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
												int _m73 = mark();
												synPredMatched73 = true;
												inputState.guessing++;
												try {
													{
													declaration_specifiers();
													function_declarator();
													match(LCURLY);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched73 = false;
												}
												rewind(_m73);
inputState.guessing--;
											}
											if ( synPredMatched73 ) {
												if ( inputState.guessing==0 ) {
													b.beginFunctionDefinition();
												}
												function_definition();
												if ( inputState.guessing==0 ) {
													b.endFunctionDefinition();
												}
											}
											else {
												boolean synPredMatched76 = false;
												if (((LA(1)==LITERAL_inline||LA(1)==OPERATOR) && (_tokenSet_28.member(LA(2))))) {
													int _m76 = mark();
													synPredMatched76 = true;
													inputState.guessing++;
													try {
														{
														{
														switch ( LA(1)) {
														case LITERAL_inline:
														{
															match(LITERAL_inline);
															break;
														}
														case OPERATOR:
														{
															break;
														}
														default:
														{
															throw new NoViableAltException(LT(1), getFilename());
														}
														}
														}
														conversion_function_decl_or_def();
														}
													}
													catch (RecognitionException pe) {
														synPredMatched76 = false;
													}
													rewind(_m76);
inputState.guessing--;
												}
												if ( synPredMatched76 ) {
													{
													switch ( LA(1)) {
													case LITERAL_inline:
													{
														match(LITERAL_inline);
														break;
													}
													case OPERATOR:
													{
														break;
													}
													default:
													{
														throw new NoViableAltException(LT(1), getFilename());
													}
													}
													}
													conversion_function_decl_or_def();
												}
												else {
													boolean synPredMatched79 = false;
													if (((_tokenSet_29.member(LA(1))) && (_tokenSet_30.member(LA(2))))) {
														int _m79 = mark();
														synPredMatched79 = true;
														inputState.guessing++;
														try {
															{
															qualified_id();
															match(SEMICOLON);
															}
														}
														catch (RecognitionException pe) {
															synPredMatched79 = false;
														}
														rewind(_m79);
inputState.guessing--;
													}
													if ( synPredMatched79 ) {
														q=qualified_id();
														match(SEMICOLON);
													}
													else {
														boolean synPredMatched81 = false;
														if (((_tokenSet_11.member(LA(1))) && (_tokenSet_31.member(LA(2))))) {
															int _m81 = mark();
															synPredMatched81 = true;
															inputState.guessing++;
															try {
																{
																declaration_specifiers();
																}
															}
															catch (RecognitionException pe) {
																synPredMatched81 = false;
															}
															rewind(_m81);
inputState.guessing--;
														}
														if ( synPredMatched81 ) {
															declaration_specifiers();
															{
															switch ( LA(1)) {
															case ID:
															case COLON:
															case LITERAL__stdcall:
															case LITERAL___stdcall:
															case LPAREN:
															case OPERATOR:
															case LITERAL_this:
															case LITERAL_true:
															case LITERAL_false:
															case STAR:
															case AMPERSAND:
															case TILDE:
															case SCOPE:
															case LITERAL__cdecl:
															case LITERAL___cdecl:
															case LITERAL__near:
															case LITERAL___near:
															case LITERAL__far:
															case LITERAL___far:
															case LITERAL___interrupt:
															case LITERAL_pascal:
															case LITERAL__pascal:
															case LITERAL___pascal:
															{
																member_declarator_list();
																break;
															}
															case SEMICOLON:
															{
																break;
															}
															default:
															{
																throw new NoViableAltException(LT(1), getFilename());
															}
															}
															}
															match(SEMICOLON);
														}
														else {
															boolean synPredMatched84 = false;
															if (((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2))))) {
																int _m84 = mark();
																synPredMatched84 = true;
																inputState.guessing++;
																try {
																	{
																	function_declarator();
																	match(SEMICOLON);
																	}
																}
																catch (RecognitionException pe) {
																	synPredMatched84 = false;
																}
																rewind(_m84);
inputState.guessing--;
															}
															if ( synPredMatched84 ) {
																function_declarator();
																match(SEMICOLON);
															}
															else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2)))) {
																function_declarator();
																compound_statement();
															}
															else {
																boolean synPredMatched87 = false;
																if (((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN))) {
																	int _m87 = mark();
																	synPredMatched87 = true;
																	inputState.guessing++;
																	try {
																		{
																		template_head();
																		declaration_specifiers();
																		{
																		switch ( LA(1)) {
																		case ID:
																		case LITERAL__stdcall:
																		case LITERAL___stdcall:
																		case LPAREN:
																		case OPERATOR:
																		case LITERAL_this:
																		case LITERAL_true:
																		case LITERAL_false:
																		case STAR:
																		case AMPERSAND:
																		case TILDE:
																		case SCOPE:
																		case LITERAL__cdecl:
																		case LITERAL___cdecl:
																		case LITERAL__near:
																		case LITERAL___near:
																		case LITERAL__far:
																		case LITERAL___far:
																		case LITERAL___interrupt:
																		case LITERAL_pascal:
																		case LITERAL__pascal:
																		case LITERAL___pascal:
																		{
																			init_declarator_list();
																			break;
																		}
																		case SEMICOLON:
																		{
																			break;
																		}
																		default:
																		{
																			throw new NoViableAltException(LT(1), getFilename());
																		}
																		}
																		}
																		match(SEMICOLON);
																		}
																	}
																	catch (RecognitionException pe) {
																		synPredMatched87 = false;
																	}
																	rewind(_m87);
inputState.guessing--;
																}
																if ( synPredMatched87 ) {
																	template_head();
																	declaration_specifiers();
																	{
																	switch ( LA(1)) {
																	case ID:
																	case LITERAL__stdcall:
																	case LITERAL___stdcall:
																	case LPAREN:
																	case OPERATOR:
																	case LITERAL_this:
																	case LITERAL_true:
																	case LITERAL_false:
																	case STAR:
																	case AMPERSAND:
																	case TILDE:
																	case SCOPE:
																	case LITERAL__cdecl:
																	case LITERAL___cdecl:
																	case LITERAL__near:
																	case LITERAL___near:
																	case LITERAL__far:
																	case LITERAL___far:
																	case LITERAL___interrupt:
																	case LITERAL_pascal:
																	case LITERAL__pascal:
																	case LITERAL___pascal:
																	{
																		init_declarator_list();
																		break;
																	}
																	case SEMICOLON:
																	{
																		break;
																	}
																	default:
																	{
																		throw new NoViableAltException(LT(1), getFilename());
																	}
																	}
																	}
																	match(SEMICOLON);
																}
																else if ((LA(1)==LITERAL_template) && (LA(2)==LESSTHAN)) {
																	template_head();
																	{
																	boolean synPredMatched91 = false;
																	if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))))) {
																		int _m91 = mark();
																		synPredMatched91 = true;
																		inputState.guessing++;
																		try {
																			{
																			ctor_decl_spec();
																			if (!(qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)))
																			  throw new SemanticException("qualifiedItemIsOneOf(CPPvariables.QI_CTOR,0)");
																			}
																		}
																		catch (RecognitionException pe) {
																			synPredMatched91 = false;
																		}
																		rewind(_m91);
inputState.guessing--;
																	}
																	if ( synPredMatched91 ) {
																		ctor_definition();
																	}
																	else {
																		boolean synPredMatched93 = false;
																		if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
																			int _m93 = mark();
																			synPredMatched93 = true;
																			inputState.guessing++;
																			try {
																				{
																				declaration_specifiers();
																				function_declarator();
																				match(SEMICOLON);
																				}
																			}
																			catch (RecognitionException pe) {
																				synPredMatched93 = false;
																			}
																			rewind(_m93);
inputState.guessing--;
																		}
																		if ( synPredMatched93 ) {
																			declaration();
																		}
																		else {
																			boolean synPredMatched95 = false;
																			if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
																				int _m95 = mark();
																				synPredMatched95 = true;
																				inputState.guessing++;
																				try {
																					{
																					declaration_specifiers();
																					function_declarator();
																					match(LCURLY);
																					}
																				}
																				catch (RecognitionException pe) {
																					synPredMatched95 = false;
																				}
																				rewind(_m95);
inputState.guessing--;
																			}
																			if ( synPredMatched95 ) {
																				function_definition();
																			}
																			else if ((LA(1)==OPERATOR) && (_tokenSet_11.member(LA(2)))) {
																				conversion_function_decl_or_def();
																			}
																			else {
																				throw new NoViableAltException(LT(1), getFilename());
																			}
																			}}
																			}
																		}
																	else {
																		throw new NoViableAltException(LT(1), getFilename());
																	}
																	}}}}}}}}}}}}}}
																	}
																	if ( inputState.guessing==0 ) {
																		b.endMemberDeclaration();
																	}
																}
																
	public final void member_declarator_list() throws RecognitionException, TokenStreamException {
		
		
		member_declarator();
		{
		switch ( LA(1)) {
		case ASSIGNEQUAL:
		{
			match(ASSIGNEQUAL);
			match(OCTALINT);
			break;
		}
		case SEMICOLON:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop179:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				member_declarator();
				{
				switch ( LA(1)) {
				case ASSIGNEQUAL:
				{
					match(ASSIGNEQUAL);
					match(OCTALINT);
					break;
				}
				case SEMICOLON:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				break _loop179;
			}
			
		} while (true);
		}
	}
	
	public final void ctor_declarator() throws RecognitionException, TokenStreamException {
		
		String q="";
		
		q=qualified_ctor_id();
		if ( inputState.guessing==0 ) {
			b.qualifiedCtorId(q);
		}
		match(LPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case ELLIPSIS:
		case SCOPE:
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		case LITERAL__near:
		case LITERAL___near:
		case LITERAL__far:
		case LITERAL___far:
		case LITERAL___interrupt:
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			parameter_list();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_throw:
		{
			exception_specification();
			break;
		}
		case LCURLY:
		case SEMICOLON:
		case COLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void compound_statement() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			b.beginCompoundStatement();
		}
		match(LCURLY);
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LCURLY:
		case SEMICOLON:
		case LITERAL_inline:
		case LITERAL_extern:
		case StringLiteral:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case LITERAL_throw:
		case LITERAL_case:
		case LITERAL_default:
		case LITERAL_if:
		case LITERAL_switch:
		case LITERAL_while:
		case LITERAL_do:
		case LITERAL_for:
		case LITERAL_goto:
		case LITERAL_continue:
		case LITERAL_break:
		case LITERAL_return:
		case LITERAL_try:
		case LITERAL_using:
		case LITERAL__asm:
		case LITERAL___asm:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			statement_list();
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			b.endCompoundStatement();
		}
	}
	
	public final void access_specifier() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_public:
		{
			match(LITERAL_public);
			if ( inputState.guessing==0 ) {
				b.accessSpecifier("public");
			}
			break;
		}
		case LITERAL_protected:
		{
			match(LITERAL_protected);
			if ( inputState.guessing==0 ) {
				b.accessSpecifier("protected");
			}
			break;
		}
		case LITERAL_private:
		{
			match(LITERAL_private);
			if ( inputState.guessing==0 ) {
				b.accessSpecifier("private");
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void linkage_specification() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_extern);
		match(StringLiteral);
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			match(LCURLY);
			{
			_loop112:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					external_declaration();
				}
				else {
					break _loop112;
				}
				
			} while (true);
			}
			match(RCURLY);
			break;
		}
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case LITERAL_using:
		case SCOPE:
		{
			declaration();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void using_declaration() throws RecognitionException, TokenStreamException {
		
		String qid="";
		
		match(LITERAL_using);
		{
		switch ( LA(1)) {
		case LITERAL_namespace:
		{
			match(LITERAL_namespace);
			qid=qualified_id();
			break;
		}
		case ID:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case SCOPE:
		{
			qid=qualified_id();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMICOLON);
	}
	
	public final void storage_class_specifier() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_auto:
		{
			match(LITERAL_auto);
			if ( inputState.guessing==0 ) {
				b.storageClassSpecifier("auto");
			}
			break;
		}
		case LITERAL_register:
		{
			match(LITERAL_register);
			if ( inputState.guessing==0 ) {
				b.storageClassSpecifier("register");
			}
			break;
		}
		case LITERAL_static:
		{
			match(LITERAL_static);
			if ( inputState.guessing==0 ) {
				b.storageClassSpecifier("static");
			}
			break;
		}
		case LITERAL_extern:
		{
			match(LITERAL_extern);
			if ( inputState.guessing==0 ) {
				b.storageClassSpecifier("extern");
			}
			break;
		}
		case LITERAL_mutable:
		{
			match(LITERAL_mutable);
			if ( inputState.guessing==0 ) {
				b.storageClassSpecifier("mutable");
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void type_qualifier() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_const:
		case LITERAL_const_cast:
		{
			{
			switch ( LA(1)) {
			case LITERAL_const:
			{
				match(LITERAL_const);
				break;
			}
			case LITERAL_const_cast:
			{
				match(LITERAL_const_cast);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				b.typeQualifier("const");
			}
			break;
		}
		case LITERAL_volatile:
		{
			match(LITERAL_volatile);
			if ( inputState.guessing==0 ) {
				b.typeQualifier("volatile");
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void type_specifier() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case ID:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case SCOPE:
		{
			simple_type_specifier();
			break;
		}
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		{
			class_specifier();
			break;
		}
		case LITERAL_enum:
		{
			enum_specifier();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void direct_declarator() throws RecognitionException, TokenStreamException {
		
		Token  dtor = null;
		String id="";
		
		switch ( LA(1)) {
		case TILDE:
		{
			match(TILDE);
			dtor = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				declaratorID(dtor.getText(),CPPvariables.QI_DTOR);
			}
			match(LPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_typedef:
			case LITERAL_enum:
			case ID:
			case LITERAL_inline:
			case LITERAL_extern:
			case LITERAL__inline:
			case LITERAL___inline:
			case LITERAL_virtual:
			case LITERAL_explicit:
			case LITERAL_friend:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_typename:
			case LITERAL_auto:
			case LITERAL_register:
			case LITERAL_static:
			case LITERAL_mutable:
			case LITERAL_const:
			case LITERAL_const_cast:
			case LITERAL_volatile:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case LITERAL_class:
			case LITERAL_struct:
			case LITERAL_union:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case ELLIPSIS:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				parameter_list();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			declarator();
			match(RPAREN);
			declarator_suffixes();
			break;
		}
		default:
			boolean synPredMatched201 = false;
			if (((_tokenSet_29.member(LA(1))) && (_tokenSet_16.member(LA(2))))) {
				int _m201 = mark();
				synPredMatched201 = true;
				inputState.guessing++;
				try {
					{
					qualified_id();
					match(LPAREN);
					{
					switch ( LA(1)) {
					case RPAREN:
					{
						match(RPAREN);
						break;
					}
					case LITERAL_typedef:
					case LITERAL_enum:
					case ID:
					case LITERAL_inline:
					case LITERAL_extern:
					case LITERAL__inline:
					case LITERAL___inline:
					case LITERAL_virtual:
					case LITERAL_explicit:
					case LITERAL_friend:
					case LITERAL__stdcall:
					case LITERAL___stdcall:
					case LITERAL__declspec:
					case LITERAL___declspec:
					case LITERAL_typename:
					case LITERAL_auto:
					case LITERAL_register:
					case LITERAL_static:
					case LITERAL_mutable:
					case LITERAL_const:
					case LITERAL_const_cast:
					case LITERAL_volatile:
					case LITERAL_char:
					case LITERAL_wchar_t:
					case LITERAL_bool:
					case LITERAL_short:
					case LITERAL_int:
					case 44:
					case 45:
					case 46:
					case LITERAL_long:
					case LITERAL_signed:
					case LITERAL_unsigned:
					case LITERAL_float:
					case LITERAL_double:
					case LITERAL_void:
					case LITERAL_class:
					case LITERAL_struct:
					case LITERAL_union:
					case SCOPE:
					{
						declaration_specifiers();
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched201 = false;
				}
				rewind(_m201);
inputState.guessing--;
			}
			if ( synPredMatched201 ) {
				id=qualified_id();
				if ( inputState.guessing==0 ) {
					declaratorID(id,CPPvariables.QI_FUN); b.directDeclarator(id);
				}
				match(LPAREN);
				{
				switch ( LA(1)) {
				case LITERAL_typedef:
				case LITERAL_enum:
				case ID:
				case LITERAL_inline:
				case LITERAL_extern:
				case LITERAL__inline:
				case LITERAL___inline:
				case LITERAL_virtual:
				case LITERAL_explicit:
				case LITERAL_friend:
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				case LITERAL__declspec:
				case LITERAL___declspec:
				case LPAREN:
				case LITERAL_typename:
				case LITERAL_auto:
				case LITERAL_register:
				case LITERAL_static:
				case LITERAL_mutable:
				case LITERAL_const:
				case LITERAL_const_cast:
				case LITERAL_volatile:
				case LITERAL_char:
				case LITERAL_wchar_t:
				case LITERAL_bool:
				case LITERAL_short:
				case LITERAL_int:
				case 44:
				case 45:
				case 46:
				case LITERAL_long:
				case LITERAL_signed:
				case LITERAL_unsigned:
				case LITERAL_float:
				case LITERAL_double:
				case LITERAL_void:
				case LITERAL_class:
				case LITERAL_struct:
				case LITERAL_union:
				case OPERATOR:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_false:
				case STAR:
				case AMPERSAND:
				case TILDE:
				case ELLIPSIS:
				case SCOPE:
				case LITERAL__cdecl:
				case LITERAL___cdecl:
				case LITERAL__near:
				case LITERAL___near:
				case LITERAL__far:
				case LITERAL___far:
				case LITERAL___interrupt:
				case LITERAL_pascal:
				case LITERAL__pascal:
				case LITERAL___pascal:
				{
					parameter_list();
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				{
				_loop204:
				do {
					if (((LA(1) >= LITERAL_const && LA(1) <= LITERAL_volatile))) {
						type_qualifier();
					}
					else {
						break _loop204;
					}
					
				} while (true);
				}
				{
				switch ( LA(1)) {
				case LITERAL_throw:
				{
					exception_specification();
					break;
				}
				case LESSTHAN:
				case GREATERTHAN:
				case ID:
				case SEMICOLON:
				case RCURLY:
				case ASSIGNEQUAL:
				case COLON:
				case COMMA:
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				case LPAREN:
				case RPAREN:
				case OPERATOR:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_false:
				case STAR:
				case AMPERSAND:
				case LSQUARE:
				case RSQUARE:
				case TILDE:
				case ELLIPSIS:
				case TIMESEQUAL:
				case DIVIDEEQUAL:
				case MINUSEQUAL:
				case PLUSEQUAL:
				case MODEQUAL:
				case SHIFTLEFTEQUAL:
				case SHIFTRIGHTEQUAL:
				case BITWISEANDEQUAL:
				case BITWISEXOREQUAL:
				case BITWISEOREQUAL:
				case QUESTIONMARK:
				case OR:
				case AND:
				case BITWISEOR:
				case BITWISEXOR:
				case NOTEQUAL:
				case EQUAL:
				case LESSTHANOREQUALTO:
				case GREATERTHANOREQUALTO:
				case SHIFTLEFT:
				case SHIFTRIGHT:
				case PLUS:
				case MINUS:
				case DIVIDE:
				case MOD:
				case DOTMBR:
				case POINTERTOMBR:
				case SCOPE:
				case LITERAL__cdecl:
				case LITERAL___cdecl:
				case LITERAL__near:
				case LITERAL___near:
				case LITERAL__far:
				case LITERAL___far:
				case LITERAL___interrupt:
				case LITERAL_pascal:
				case LITERAL__pascal:
				case LITERAL___pascal:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				boolean synPredMatched207 = false;
				if (((_tokenSet_29.member(LA(1))) && (_tokenSet_16.member(LA(2))))) {
					int _m207 = mark();
					synPredMatched207 = true;
					inputState.guessing++;
					try {
						{
						qualified_id();
						match(LPAREN);
						qualified_id();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched207 = false;
					}
					rewind(_m207);
inputState.guessing--;
				}
				if ( synPredMatched207 ) {
					id=qualified_id();
					if ( inputState.guessing==0 ) {
						declaratorID(id,CPPvariables.QI_VAR);
					}
					match(LPAREN);
					expression_list();
					match(RPAREN);
				}
				else {
					boolean synPredMatched209 = false;
					if (((_tokenSet_29.member(LA(1))) && (_tokenSet_16.member(LA(2))))) {
						int _m209 = mark();
						synPredMatched209 = true;
						inputState.guessing++;
						try {
							{
							qualified_id();
							match(LSQUARE);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched209 = false;
						}
						rewind(_m209);
inputState.guessing--;
					}
					if ( synPredMatched209 ) {
						id=qualified_id();
						if ( inputState.guessing==0 ) {
							
							if (_td==true)
							declaratorID(id,CPPvariables.QI_TYPE);
							else
							declaratorID(id,CPPvariables.QI_VAR);
							
						}
						{
						int _cnt212=0;
						_loop212:
						do {
							if ((LA(1)==LSQUARE) && (_tokenSet_32.member(LA(2)))) {
								match(LSQUARE);
								{
								switch ( LA(1)) {
								case ID:
								case StringLiteral:
								case LITERAL__declspec:
								case LITERAL___declspec:
								case LPAREN:
								case LITERAL_const_cast:
								case LITERAL_char:
								case LITERAL_wchar_t:
								case LITERAL_bool:
								case LITERAL_short:
								case LITERAL_int:
								case 44:
								case 45:
								case 46:
								case LITERAL_long:
								case LITERAL_signed:
								case LITERAL_unsigned:
								case LITERAL_float:
								case LITERAL_double:
								case LITERAL_void:
								case OPERATOR:
								case LITERAL_this:
								case LITERAL_true:
								case LITERAL_false:
								case OCTALINT:
								case STAR:
								case AMPERSAND:
								case TILDE:
								case PLUS:
								case MINUS:
								case PLUSPLUS:
								case MINUSMINUS:
								case LITERAL_sizeof:
								case SCOPE:
								case LITERAL_dynamic_cast:
								case LITERAL_static_cast:
								case LITERAL_reinterpret_cast:
								case NOT:
								case LITERAL_new:
								case LITERAL_delete:
								case DECIMALINT:
								case HEXADECIMALINT:
								case CharLiteral:
								case FLOATONE:
								case FLOATTWO:
								{
									constant_expression();
									break;
								}
								case RSQUARE:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								}
								}
								match(RSQUARE);
							}
							else {
								if ( _cnt212>=1 ) { break _loop212; } else {throw new NoViableAltException(LT(1), getFilename());}
							}
							
							_cnt212++;
						} while (true);
						}
					}
					else if ((_tokenSet_29.member(LA(1))) && (_tokenSet_33.member(LA(2)))) {
						id=qualified_id();
						if ( inputState.guessing==0 ) {
							
							if (_td==true)
							declaratorID(id,CPPvariables.QI_TYPE);
							else {
							declaratorID(id,CPPvariables.QI_VAR);
							b.directDeclarator(id);
							}
							
						}
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}}
			}
			
	public final void simple_type_specifier() throws RecognitionException, TokenStreamException {
		
		String s="";
		java.util.BitSet auxBitSet=(java.util.BitSet)CPPvariables.QI_TYPE.clone();
		auxBitSet.or(CPPvariables.QI_CTOR);
		List sts = new ArrayList();
		
		
		{
		if (((LA(1)==ID||LA(1)==SCOPE))&&(qualifiedItemIsOneOf(auxBitSet,0))) {
			s=qualified_type();
			if ( inputState.guessing==0 ) {
				sts.add(s); b.simpleTypeSpecifier(sts);
			}
		}
		else if ((_tokenSet_34.member(LA(1)))) {
			{
			int _cnt129=0;
			_loop129:
			do {
				switch ( LA(1)) {
				case LITERAL_char:
				{
					match(LITERAL_char);
					if ( inputState.guessing==0 ) {
						sts.add("char");
					}
					break;
				}
				case LITERAL_wchar_t:
				{
					match(LITERAL_wchar_t);
					if ( inputState.guessing==0 ) {
						sts.add("wchar_t");
					}
					break;
				}
				case LITERAL_bool:
				{
					match(LITERAL_bool);
					if ( inputState.guessing==0 ) {
						sts.add("bool");
					}
					break;
				}
				case LITERAL_short:
				{
					match(LITERAL_short);
					if ( inputState.guessing==0 ) {
						sts.add("short");
					}
					break;
				}
				case LITERAL_int:
				{
					match(LITERAL_int);
					if ( inputState.guessing==0 ) {
						sts.add("int");
					}
					break;
				}
				case 44:
				case 45:
				{
					{
					switch ( LA(1)) {
					case 44:
					{
						match(44);
						break;
					}
					case 45:
					{
						match(45);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						sts.add("__int64");
					}
					break;
				}
				case 46:
				{
					match(46);
					if ( inputState.guessing==0 ) {
						sts.add("__w64");
					}
					break;
				}
				case LITERAL_long:
				{
					match(LITERAL_long);
					if ( inputState.guessing==0 ) {
						sts.add("long");
					}
					break;
				}
				case LITERAL_signed:
				{
					match(LITERAL_signed);
					if ( inputState.guessing==0 ) {
						sts.add("signed");
					}
					break;
				}
				case LITERAL_unsigned:
				{
					match(LITERAL_unsigned);
					if ( inputState.guessing==0 ) {
						sts.add("unsigned");
					}
					break;
				}
				case LITERAL_float:
				{
					match(LITERAL_float);
					if ( inputState.guessing==0 ) {
						sts.add("float");
					}
					break;
				}
				case LITERAL_double:
				{
					match(LITERAL_double);
					if ( inputState.guessing==0 ) {
						sts.add("double");
					}
					break;
				}
				case LITERAL_void:
				{
					match(LITERAL_void);
					if ( inputState.guessing==0 ) {
						sts.add("void");
					}
					break;
				}
				case LITERAL__declspec:
				case LITERAL___declspec:
				{
					{
					switch ( LA(1)) {
					case LITERAL__declspec:
					{
						match(LITERAL__declspec);
						break;
					}
					case LITERAL___declspec:
					{
						match(LITERAL___declspec);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LPAREN);
					match(ID);
					match(RPAREN);
					break;
				}
				default:
				{
					if ( _cnt129>=1 ) { break _loop129; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				}
				_cnt129++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				b.simpleTypeSpecifier(sts);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void class_specifier() throws RecognitionException, TokenStreamException {
		
		String saveClass="";String id="";String type="";
		
		{
		switch ( LA(1)) {
		case LITERAL_class:
		{
			match(LITERAL_class);
			if ( inputState.guessing==0 ) {
				type=CPPvariables.OT_CLASS;
			}
			break;
		}
		case LITERAL_struct:
		{
			match(LITERAL_struct);
			if ( inputState.guessing==0 ) {
				type=CPPvariables.OT_STRUCT;
			}
			break;
		}
		case LITERAL_union:
		{
			match(LITERAL_union);
			if ( inputState.guessing==0 ) {
				type=CPPvariables.OT_UNION;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case ID:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case SCOPE:
		{
			id=qualified_id();
			{
			if ((LA(1)==LCURLY||LA(1)==COLON) && (_tokenSet_35.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					saveClass = enclosingClass;
					enclosingClass = id;
					
				}
				if ( inputState.guessing==0 ) {
					b.beginClassDefinition(type, id);
				}
				{
				switch ( LA(1)) {
				case COLON:
				{
					base_clause();
					break;
				}
				case LCURLY:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(LCURLY);
				if ( inputState.guessing==0 ) {
					
					if(!symbols.containsKey(id))
					symbols.put(id,type);
					
				}
				{
				_loop138:
				do {
					if ((_tokenSet_36.member(LA(1)))) {
						member_declaration();
					}
					else {
						break _loop138;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					b.endClassDefinition();
				}
				match(RCURLY);
				if ( inputState.guessing==0 ) {
					enclosingClass = saveClass;
				}
			}
			else if ((_tokenSet_37.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					
					
					String auxName=id;
					int pos = auxName.indexOf("::");
					while(pos>=0)
					{
					if(!symbols.containsKey(auxName.substring(0,pos)))
					symbols.put(auxName.substring(0,pos),type);
					auxName=auxName.substring(pos+2,auxName.length());
					pos=auxName.indexOf("::");
					}
					if(!symbols.containsKey(auxName))
					symbols.put(auxName,type);
					
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		case LCURLY:
		{
			match(LCURLY);
			if ( inputState.guessing==0 ) {
				id="anonymous";
				saveClass = enclosingClass; enclosingClass = "anonymous";
				if(!symbols.containsKey(id))
				symbols.put(id,type);
				
			}
			{
			_loop140:
			do {
				if ((_tokenSet_36.member(LA(1)))) {
					member_declaration();
				}
				else {
					break _loop140;
				}
				
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				enclosingClass = saveClass;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final String  qualified_type() throws RecognitionException, TokenStreamException {
		String q="";
		
		Token  id = null;
		String s=""; String  qitem="";
		
		s=scope_override();
		id = LT(1);
		match(ID);
		{
		if ((LA(1)==LESSTHAN) && (_tokenSet_24.member(LA(2)))) {
			match(LESSTHAN);
			template_argument_list();
			match(GREATERTHAN);
		}
		else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			
			qitem=s;
			qitem=qitem+id.getText();
			q=qitem;
			
		}
		return q;
	}
	
	public final void template_argument_list() throws RecognitionException, TokenStreamException {
		
		
		template_argument();
		{
		_loop302:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				template_argument();
			}
			else {
				break _loop302;
			}
			
		} while (true);
		}
	}
	
	public final void base_clause() throws RecognitionException, TokenStreamException {
		
		
		match(COLON);
		base_specifier();
		{
		_loop170:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				base_specifier();
			}
			else {
				break _loop170;
			}
			
		} while (true);
		}
	}
	
	public final void enumerator_list() throws RecognitionException, TokenStreamException {
		
		
		enumerator();
		{
		_loop146:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				enumerator();
			}
			else {
				break _loop146;
			}
			
		} while (true);
		}
	}
	
	public final void enumerator() throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		
		id = LT(1);
		match(ID);
		{
		switch ( LA(1)) {
		case ASSIGNEQUAL:
		{
			match(ASSIGNEQUAL);
			constant_expression();
			break;
		}
		case RCURLY:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void constant_expression() throws RecognitionException, TokenStreamException {
		
		
		conditional_expression();
	}
	
	public final void optor() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_new:
		{
			match(LITERAL_new);
			{
			if ((LA(1)==LSQUARE) && (LA(2)==RSQUARE)) {
				match(LSQUARE);
				match(RSQUARE);
			}
			else if ((_tokenSet_38.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		case LITERAL_delete:
		{
			match(LITERAL_delete);
			{
			if ((LA(1)==LSQUARE) && (LA(2)==RSQUARE)) {
				match(LSQUARE);
				match(RSQUARE);
			}
			else if ((_tokenSet_38.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			match(RPAREN);
			break;
		}
		case LSQUARE:
		{
			match(LSQUARE);
			match(RSQUARE);
			break;
		}
		case LESSTHAN:
		case GREATERTHAN:
		case ASSIGNEQUAL:
		case COMMA:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case TIMESEQUAL:
		case DIVIDEEQUAL:
		case MINUSEQUAL:
		case PLUSEQUAL:
		case MODEQUAL:
		case SHIFTLEFTEQUAL:
		case SHIFTRIGHTEQUAL:
		case BITWISEANDEQUAL:
		case BITWISEXOREQUAL:
		case BITWISEOREQUAL:
		case OR:
		case AND:
		case BITWISEOR:
		case BITWISEXOR:
		case NOTEQUAL:
		case EQUAL:
		case LESSTHANOREQUALTO:
		case GREATERTHANOREQUALTO:
		case SHIFTLEFT:
		case SHIFTRIGHT:
		case PLUS:
		case MINUS:
		case DIVIDE:
		case MOD:
		case POINTERTOMBR:
		case PLUSPLUS:
		case MINUSMINUS:
		case POINTERTO:
		case NOT:
		{
			optor_simple_tokclass();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void typeID() throws RecognitionException, TokenStreamException {
		
		
		if (!(isTypeName(LT(1).getText())))
		  throw new SemanticException("isTypeName(LT(1).getText())");
		match(ID);
	}
	
	public final void init_declarator() throws RecognitionException, TokenStreamException {
		
		
		declarator();
		{
		switch ( LA(1)) {
		case ASSIGNEQUAL:
		{
			match(ASSIGNEQUAL);
			initializer();
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			expression_list();
			match(RPAREN);
			break;
		}
		case SEMICOLON:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void declarator() throws RecognitionException, TokenStreamException {
		
		
		boolean synPredMatched197 = false;
		if (((_tokenSet_13.member(LA(1))) && (_tokenSet_39.member(LA(2))))) {
			int _m197 = mark();
			synPredMatched197 = true;
			inputState.guessing++;
			try {
				{
				ptr_operator();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched197 = false;
			}
			rewind(_m197);
inputState.guessing--;
		}
		if ( synPredMatched197 ) {
			ptr_operator();
			declarator();
		}
		else if ((_tokenSet_40.member(LA(1))) && (_tokenSet_41.member(LA(2)))) {
			direct_declarator();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void initializer() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			b.beginInitializer();
		}
		{
		switch ( LA(1)) {
		case ID:
		case StringLiteral:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_const_cast:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			remainder_expression();
			break;
		}
		case LCURLY:
		{
			match(LCURLY);
			initializer();
			{
			_loop162:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					initializer();
				}
				else {
					break _loop162;
				}
				
			} while (true);
			}
			match(RCURLY);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			b.endInitializer();
		}
	}
	
	public final void expression_list() throws RecognitionException, TokenStreamException {
		
		
		assignment_expression();
		{
		_loop463:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				assignment_expression();
			}
			else {
				break _loop463;
			}
			
		} while (true);
		}
	}
	
	public final void remainder_expression() throws RecognitionException, TokenStreamException {
		
		
		{
		boolean synPredMatched353 = false;
		if (((_tokenSet_42.member(LA(1))) && (_tokenSet_43.member(LA(2))))) {
			int _m353 = mark();
			synPredMatched353 = true;
			inputState.guessing++;
			try {
				{
				conditional_expression();
				{
				switch ( LA(1)) {
				case COMMA:
				{
					match(COMMA);
					break;
				}
				case SEMICOLON:
				{
					match(SEMICOLON);
					break;
				}
				case RPAREN:
				{
					match(RPAREN);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched353 = false;
			}
			rewind(_m353);
inputState.guessing--;
		}
		if ( synPredMatched353 ) {
			assignment_expression();
		}
		else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_43.member(LA(2)))) {
			assignment_expression();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void base_specifier() throws RecognitionException, TokenStreamException {
		
		String qt=""; b.beginBaseSpecifier();
		
		{
		switch ( LA(1)) {
		case LITERAL_virtual:
		{
			match(LITERAL_virtual);
			{
			switch ( LA(1)) {
			case LITERAL_public:
			case LITERAL_protected:
			case LITERAL_private:
			{
				access_specifier();
				break;
			}
			case ID:
			case SCOPE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			qt=qualified_type();
			if ( inputState.guessing==0 ) {
				b.baseSpecifier(qt, true);
			}
			break;
		}
		case ID:
		case SCOPE:
		{
			qt=qualified_type();
			if ( inputState.guessing==0 ) {
				b.baseSpecifier(qt, false);
			}
			break;
		}
		default:
			if (((LA(1) >= LITERAL_public && LA(1) <= LITERAL_private)) && (LA(2)==LITERAL_virtual)) {
				access_specifier();
				match(LITERAL_virtual);
				qt=qualified_type();
				if ( inputState.guessing==0 ) {
					b.baseSpecifier(qt, true);
				}
			}
			else if (((LA(1) >= LITERAL_public && LA(1) <= LITERAL_private)) && (LA(2)==ID||LA(2)==SCOPE)) {
				access_specifier();
				qt=qualified_type();
				if ( inputState.guessing==0 ) {
					b.baseSpecifier(qt, false);
				}
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			b.endBaseSpecifier();
		}
	}
	
	public final void member_declarator() throws RecognitionException, TokenStreamException {
		
		
		boolean synPredMatched183 = false;
		if (((LA(1)==ID||LA(1)==COLON) && (_tokenSet_44.member(LA(2))))) {
			int _m183 = mark();
			synPredMatched183 = true;
			inputState.guessing++;
			try {
				{
				{
				switch ( LA(1)) {
				case ID:
				{
					match(ID);
					break;
				}
				case COLON:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(COLON);
				constant_expression();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched183 = false;
			}
			rewind(_m183);
inputState.guessing--;
		}
		if ( synPredMatched183 ) {
			{
			switch ( LA(1)) {
			case ID:
			{
				match(ID);
				break;
			}
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(COLON);
			constant_expression();
		}
		else if ((_tokenSet_45.member(LA(1))) && (_tokenSet_46.member(LA(2)))) {
			declarator();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void template_parameter_list() throws RecognitionException, TokenStreamException {
		
		
		template_parameter();
		{
		_loop291:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				template_parameter();
			}
			else {
				break _loop291;
			}
			
		} while (true);
		}
	}
	
	public final void parameter_list() throws RecognitionException, TokenStreamException {
		
		
		parameter_declaration_list();
		{
		switch ( LA(1)) {
		case ELLIPSIS:
		{
			match(ELLIPSIS);
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void exception_specification() throws RecognitionException, TokenStreamException {
		
		String so="";
		
		match(LITERAL_throw);
		match(LPAREN);
		{
		switch ( LA(1)) {
		case ID:
		case RPAREN:
		case SCOPE:
		{
			{
			switch ( LA(1)) {
			case ID:
			case SCOPE:
			{
				so=scope_override();
				match(ID);
				{
				_loop287:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						so=scope_override();
						match(ID);
					}
					else {
						break _loop287;
					}
					
				} while (true);
				}
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case ELLIPSIS:
		{
			match(ELLIPSIS);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
	}
	
	public final void cv_qualifier_seq() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop194:
		do {
			if (((LA(1) >= LITERAL_const && LA(1) <= LITERAL_volatile))) {
				type_qualifier();
			}
			else {
				break _loop194;
			}
			
		} while (true);
		}
	}
	
	public final void ptr_operator() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			b.beginPtrOperator();
		}
		{
		switch ( LA(1)) {
		case AMPERSAND:
		{
			match(AMPERSAND);
			if ( inputState.guessing==0 ) {
				b.ptrOperator("&");
			}
			break;
		}
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		{
			{
			switch ( LA(1)) {
			case LITERAL__cdecl:
			{
				match(LITERAL__cdecl);
				break;
			}
			case LITERAL___cdecl:
			{
				match(LITERAL___cdecl);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LITERAL__near:
		case LITERAL___near:
		{
			{
			switch ( LA(1)) {
			case LITERAL__near:
			{
				match(LITERAL__near);
				break;
			}
			case LITERAL___near:
			{
				match(LITERAL___near);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LITERAL__far:
		case LITERAL___far:
		{
			{
			switch ( LA(1)) {
			case LITERAL__far:
			{
				match(LITERAL__far);
				break;
			}
			case LITERAL___far:
			{
				match(LITERAL___far);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LITERAL___interrupt:
		{
			match(LITERAL___interrupt);
			break;
		}
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			{
			switch ( LA(1)) {
			case LITERAL_pascal:
			{
				match(LITERAL_pascal);
				break;
			}
			case LITERAL__pascal:
			{
				match(LITERAL__pascal);
				break;
			}
			case LITERAL___pascal:
			{
				match(LITERAL___pascal);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		{
			{
			switch ( LA(1)) {
			case LITERAL__stdcall:
			{
				match(LITERAL__stdcall);
				break;
			}
			case LITERAL___stdcall:
			{
				match(LITERAL___stdcall);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case ID:
		case STAR:
		case SCOPE:
		{
			ptr_to_member();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			b.endPtrOperator();
		}
	}
	
	public final void declarator_suffixes() throws RecognitionException, TokenStreamException {
		
		java.util.BitSet auxBitSet=(java.util.BitSet)CPPvariables.QI_TYPE.clone(); auxBitSet.or(CPPvariables.QI_CTOR);
		
		{
		if ((LA(1)==LSQUARE)) {
			{
			int _cnt218=0;
			_loop218:
			do {
				if ((LA(1)==LSQUARE) && (_tokenSet_32.member(LA(2)))) {
					match(LSQUARE);
					{
					switch ( LA(1)) {
					case ID:
					case StringLiteral:
					case LITERAL__declspec:
					case LITERAL___declspec:
					case LPAREN:
					case LITERAL_const_cast:
					case LITERAL_char:
					case LITERAL_wchar_t:
					case LITERAL_bool:
					case LITERAL_short:
					case LITERAL_int:
					case 44:
					case 45:
					case 46:
					case LITERAL_long:
					case LITERAL_signed:
					case LITERAL_unsigned:
					case LITERAL_float:
					case LITERAL_double:
					case LITERAL_void:
					case OPERATOR:
					case LITERAL_this:
					case LITERAL_true:
					case LITERAL_false:
					case OCTALINT:
					case STAR:
					case AMPERSAND:
					case TILDE:
					case PLUS:
					case MINUS:
					case PLUSPLUS:
					case MINUSMINUS:
					case LITERAL_sizeof:
					case SCOPE:
					case LITERAL_dynamic_cast:
					case LITERAL_static_cast:
					case LITERAL_reinterpret_cast:
					case NOT:
					case LITERAL_new:
					case LITERAL_delete:
					case DECIMALINT:
					case HEXADECIMALINT:
					case CharLiteral:
					case FLOATONE:
					case FLOATTWO:
					{
						constant_expression();
						break;
					}
					case RSQUARE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(RSQUARE);
				}
				else {
					if ( _cnt218>=1 ) { break _loop218; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt218++;
			} while (true);
			}
		}
		else if (((LA(1)==LPAREN))&&((!((LA(1)==LPAREN)&&(LA(2)==ID))||(qualifiedItemIsOneOf(auxBitSet,1))))) {
			match(LPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_typedef:
			case LITERAL_enum:
			case ID:
			case LITERAL_inline:
			case LITERAL_extern:
			case LITERAL__inline:
			case LITERAL___inline:
			case LITERAL_virtual:
			case LITERAL_explicit:
			case LITERAL_friend:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_typename:
			case LITERAL_auto:
			case LITERAL_register:
			case LITERAL_static:
			case LITERAL_mutable:
			case LITERAL_const:
			case LITERAL_const_cast:
			case LITERAL_volatile:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case LITERAL_class:
			case LITERAL_struct:
			case LITERAL_union:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case ELLIPSIS:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				parameter_list();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			{
			_loop221:
			do {
				if (((LA(1) >= LITERAL_const && LA(1) <= LITERAL_volatile))) {
					type_qualifier();
				}
				else {
					break _loop221;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case LITERAL_throw:
			{
				exception_specification();
				break;
			}
			case LESSTHAN:
			case GREATERTHAN:
			case ID:
			case SEMICOLON:
			case RCURLY:
			case ASSIGNEQUAL:
			case COLON:
			case COMMA:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LPAREN:
			case RPAREN:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case LSQUARE:
			case RSQUARE:
			case TILDE:
			case ELLIPSIS:
			case TIMESEQUAL:
			case DIVIDEEQUAL:
			case MINUSEQUAL:
			case PLUSEQUAL:
			case MODEQUAL:
			case SHIFTLEFTEQUAL:
			case SHIFTRIGHTEQUAL:
			case BITWISEANDEQUAL:
			case BITWISEXOREQUAL:
			case BITWISEOREQUAL:
			case QUESTIONMARK:
			case OR:
			case AND:
			case BITWISEOR:
			case BITWISEXOR:
			case NOTEQUAL:
			case EQUAL:
			case LESSTHANOREQUALTO:
			case GREATERTHANOREQUALTO:
			case SHIFTLEFT:
			case SHIFTRIGHT:
			case PLUS:
			case MINUS:
			case DIVIDE:
			case MOD:
			case DOTMBR:
			case POINTERTOMBR:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void function_direct_declarator() throws RecognitionException, TokenStreamException {
		
		String q="";
		
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			q=qualified_id();
			if ( inputState.guessing==0 ) {
				
				declaratorID(q,CPPvariables.QI_FUN);
				
			}
			match(RPAREN);
			break;
		}
		case ID:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case SCOPE:
		{
			q=qualified_id();
			if ( inputState.guessing==0 ) {
				
				declaratorID(q,CPPvariables.QI_FUN);
				
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			b.functionDirectDeclarator(q);
		}
		match(LPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case ELLIPSIS:
		case SCOPE:
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		case LITERAL__near:
		case LITERAL___near:
		case LITERAL__far:
		case LITERAL___far:
		case LITERAL___interrupt:
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			parameter_list();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		{
		_loop230:
		do {
			if (((LA(1) >= LITERAL_const && LA(1) <= LITERAL_volatile)) && (_tokenSet_47.member(LA(2)))) {
				type_qualifier();
			}
			else {
				break _loop230;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case ASSIGNEQUAL:
		{
			match(ASSIGNEQUAL);
			match(OCTALINT);
			break;
		}
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LCURLY:
		case SEMICOLON:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case LITERAL_throw:
		case LITERAL_using:
		case SCOPE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LITERAL_throw:
		{
			exception_specification();
			break;
		}
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LCURLY:
		case SEMICOLON:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case LITERAL_using:
		case SCOPE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void ctor_head() throws RecognitionException, TokenStreamException {
		
		
		ctor_decl_spec();
		ctor_declarator();
	}
	
	public final void ctor_body() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case COLON:
		{
			ctor_initializer();
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		compound_statement();
	}
	
	public final String  qualified_ctor_id() throws RecognitionException, TokenStreamException {
		String q="";
		
		Token  id = null;
		
		String so="";
		String qitem="";
		
		
		so=scope_override();
		if ( inputState.guessing==0 ) {
			qitem=so;
		}
		id = LT(1);
		match(ID);
		if ( inputState.guessing==0 ) {
			qitem=qitem+id.getText();
			q = qitem;
		}
		return q;
	}
	
	public final void ctor_initializer() throws RecognitionException, TokenStreamException {
		
		
		match(COLON);
		superclass_init();
		{
		_loop247:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				superclass_init();
			}
			else {
				break _loop247;
			}
			
		} while (true);
		}
	}
	
	public final void superclass_init() throws RecognitionException, TokenStreamException {
		
		String q="";
		
		q=qualified_id();
		match(LPAREN);
		{
		switch ( LA(1)) {
		case ID:
		case StringLiteral:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_const_cast:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			expression_list();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
	}
	
	public final void dtor_decl_spec() throws RecognitionException, TokenStreamException {
		
		List declSpecs = new ArrayList();
		
		{
		_loop254:
		do {
			switch ( LA(1)) {
			case LITERAL_inline:
			case LITERAL__inline:
			case LITERAL___inline:
			{
				{
				switch ( LA(1)) {
				case LITERAL_inline:
				{
					match(LITERAL_inline);
					break;
				}
				case LITERAL__inline:
				{
					match(LITERAL__inline);
					break;
				}
				case LITERAL___inline:
				{
					match(LITERAL___inline);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					declSpecs.add("inline");
				}
				break;
			}
			case LITERAL_virtual:
			{
				match(LITERAL_virtual);
				if ( inputState.guessing==0 ) {
					declSpecs.add("virtual");
				}
				break;
			}
			default:
			{
				break _loop254;
			}
			}
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			b.declarationSpecifiers(declSpecs);
		}
	}
	
	public final void dtor_declarator() throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		String s="";
		
		s=scope_override();
		match(TILDE);
		id = LT(1);
		match(ID);
		if ( inputState.guessing==0 ) {
			b.dtorDeclarator(s+"~"+id.getText());
		}
		match(LPAREN);
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_throw:
		{
			exception_specification();
			break;
		}
		case LCURLY:
		case SEMICOLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void parameter_declaration_list() throws RecognitionException, TokenStreamException {
		
		
		{
		parameter_declaration();
		{
		_loop263:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				parameter_declaration();
			}
			else {
				break _loop263;
			}
			
		} while (true);
		}
		}
	}
	
	public final void parameter_declaration() throws RecognitionException, TokenStreamException {
		
		java.util.BitSet auxBitSet=(java.util.BitSet)CPPvariables.QI_TYPE.clone(); auxBitSet.or(CPPvariables.QI_CTOR);
		
		if ( inputState.guessing==0 ) {
			b.beginParameterDeclaration();
		}
		{
		if (((_tokenSet_11.member(LA(1))) && (_tokenSet_48.member(LA(2))))&&(!((LA(1)==SCOPE) && (LA(2)==STAR||LA(2)==OPERATOR))&&( !(LA(1)==SCOPE||LA(1)==ID) || qualifiedItemIsOneOf(auxBitSet,0) ))) {
			declaration_specifiers();
			{
			boolean synPredMatched268 = false;
			if (((_tokenSet_45.member(LA(1))) && (_tokenSet_49.member(LA(2))))) {
				int _m268 = mark();
				synPredMatched268 = true;
				inputState.guessing++;
				try {
					{
					declarator();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched268 = false;
				}
				rewind(_m268);
inputState.guessing--;
			}
			if ( synPredMatched268 ) {
				declarator();
			}
			else if ((_tokenSet_50.member(LA(1))) && (_tokenSet_51.member(LA(2)))) {
				abstract_declarator();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else {
			boolean synPredMatched270 = false;
			if (((_tokenSet_45.member(LA(1))) && (_tokenSet_49.member(LA(2))))) {
				int _m270 = mark();
				synPredMatched270 = true;
				inputState.guessing++;
				try {
					{
					declarator();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched270 = false;
				}
				rewind(_m270);
inputState.guessing--;
			}
			if ( synPredMatched270 ) {
				declarator();
			}
			else if ((LA(1)==ELLIPSIS)) {
				match(ELLIPSIS);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case ASSIGNEQUAL:
			{
				match(ASSIGNEQUAL);
				remainder_expression();
				break;
			}
			case GREATERTHAN:
			case COMMA:
			case RPAREN:
			case ELLIPSIS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				b.endParameterDeclaration();
			}
		}
		
	public final void abstract_declarator() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case ID:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case STAR:
		case AMPERSAND:
		case SCOPE:
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		case LITERAL__near:
		case LITERAL___near:
		case LITERAL__far:
		case LITERAL___far:
		case LITERAL___interrupt:
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			ptr_operator();
			abstract_declarator();
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			abstract_declarator();
			match(RPAREN);
			{
			int _cnt275=0;
			_loop275:
			do {
				if ((LA(1)==LPAREN||LA(1)==LSQUARE)) {
					abstract_declarator_suffix();
				}
				else {
					if ( _cnt275>=1 ) { break _loop275; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt275++;
			} while (true);
			}
			break;
		}
		case LSQUARE:
		{
			{
			int _cnt278=0;
			_loop278:
			do {
				if ((LA(1)==LSQUARE)) {
					match(LSQUARE);
					{
					switch ( LA(1)) {
					case ID:
					case StringLiteral:
					case LITERAL__declspec:
					case LITERAL___declspec:
					case LPAREN:
					case LITERAL_const_cast:
					case LITERAL_char:
					case LITERAL_wchar_t:
					case LITERAL_bool:
					case LITERAL_short:
					case LITERAL_int:
					case 44:
					case 45:
					case 46:
					case LITERAL_long:
					case LITERAL_signed:
					case LITERAL_unsigned:
					case LITERAL_float:
					case LITERAL_double:
					case LITERAL_void:
					case OPERATOR:
					case LITERAL_this:
					case LITERAL_true:
					case LITERAL_false:
					case OCTALINT:
					case STAR:
					case AMPERSAND:
					case TILDE:
					case PLUS:
					case MINUS:
					case PLUSPLUS:
					case MINUSMINUS:
					case LITERAL_sizeof:
					case SCOPE:
					case LITERAL_dynamic_cast:
					case LITERAL_static_cast:
					case LITERAL_reinterpret_cast:
					case NOT:
					case LITERAL_new:
					case LITERAL_delete:
					case DECIMALINT:
					case HEXADECIMALINT:
					case CharLiteral:
					case FLOATONE:
					case FLOATTWO:
					{
						constant_expression();
						break;
					}
					case RSQUARE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(RSQUARE);
				}
				else {
					if ( _cnt278>=1 ) { break _loop278; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt278++;
			} while (true);
			}
			break;
		}
		case GREATERTHAN:
		case ASSIGNEQUAL:
		case COMMA:
		case RPAREN:
		case ELLIPSIS:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void type_name() throws RecognitionException, TokenStreamException {
		
		
		declaration_specifiers();
		abstract_declarator();
	}
	
	public final void abstract_declarator_suffix() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LSQUARE:
		{
			match(LSQUARE);
			{
			switch ( LA(1)) {
			case ID:
			case StringLiteral:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_const_cast:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case OCTALINT:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case PLUS:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LITERAL_sizeof:
			case SCOPE:
			case LITERAL_dynamic_cast:
			case LITERAL_static_cast:
			case LITERAL_reinterpret_cast:
			case NOT:
			case LITERAL_new:
			case LITERAL_delete:
			case DECIMALINT:
			case HEXADECIMALINT:
			case CharLiteral:
			case FLOATONE:
			case FLOATTWO:
			{
				constant_expression();
				break;
			}
			case RSQUARE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RSQUARE);
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_typedef:
			case LITERAL_enum:
			case ID:
			case LITERAL_inline:
			case LITERAL_extern:
			case LITERAL__inline:
			case LITERAL___inline:
			case LITERAL_virtual:
			case LITERAL_explicit:
			case LITERAL_friend:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_typename:
			case LITERAL_auto:
			case LITERAL_register:
			case LITERAL_static:
			case LITERAL_mutable:
			case LITERAL_const:
			case LITERAL_const_cast:
			case LITERAL_volatile:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case LITERAL_class:
			case LITERAL_struct:
			case LITERAL_union:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case ELLIPSIS:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				parameter_list();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			cv_qualifier_seq();
			{
			switch ( LA(1)) {
			case LITERAL_throw:
			{
				exception_specification();
				break;
			}
			case GREATERTHAN:
			case ASSIGNEQUAL:
			case COMMA:
			case LPAREN:
			case RPAREN:
			case LSQUARE:
			case ELLIPSIS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void template_parameter() throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		
		{
		if ((LA(1)==LITERAL_typename||LA(1)==LITERAL_class) && (_tokenSet_52.member(LA(2)))) {
			{
			switch ( LA(1)) {
			case LITERAL_class:
			{
				match(LITERAL_class);
				break;
			}
			case LITERAL_typename:
			{
				match(LITERAL_typename);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case ID:
			{
				id = LT(1);
				match(ID);
				{
				switch ( LA(1)) {
				case ASSIGNEQUAL:
				{
					match(ASSIGNEQUAL);
					assigned_type_name();
					break;
				}
				case GREATERTHAN:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case GREATERTHAN:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				if(!symbols.containsKey(id.getText()))
				symbols.put(id.getText(),CPPvariables.OT_TYPE_DEF);
				
			}
		}
		else if ((_tokenSet_53.member(LA(1))) && (_tokenSet_10.member(LA(2)))) {
			parameter_declaration();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void assigned_type_name() throws RecognitionException, TokenStreamException {
		
		String s="";
		
		{
		if ((LA(1)==ID||LA(1)==SCOPE) && (_tokenSet_54.member(LA(2)))) {
			s=qualified_type();
			abstract_declarator();
		}
		else if ((_tokenSet_55.member(LA(1))) && (_tokenSet_56.member(LA(2)))) {
			simple_type_specifier();
			abstract_declarator();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void template_id() throws RecognitionException, TokenStreamException {
		
		
		match(ID);
		match(LESSTHAN);
		template_argument_list();
		match(GREATERTHAN);
	}
	
	public final void template_argument() throws RecognitionException, TokenStreamException {
		
		java.util.BitSet auxBitSet=(java.util.BitSet)CPPvariables.QI_TYPE.clone(); auxBitSet.or(CPPvariables.QI_CTOR);
		
		if (((_tokenSet_11.member(LA(1))) && (_tokenSet_57.member(LA(2))))&&(( !(LA(1)==SCOPE||LA(1)==ID) || qualifiedItemIsOneOf(auxBitSet,0) ))) {
			type_name();
		}
		else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_58.member(LA(2)))) {
			shift_expression();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void shift_expression() throws RecognitionException, TokenStreamException {
		
		
		additive_expression();
		{
		_loop383:
		do {
			if ((LA(1)==SHIFTLEFT||LA(1)==SHIFTRIGHT)) {
				{
				switch ( LA(1)) {
				case SHIFTLEFT:
				{
					match(SHIFTLEFT);
					break;
				}
				case SHIFTRIGHT:
				{
					match(SHIFTRIGHT);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				additive_expression();
			}
			else {
				break _loop383;
			}
			
		} while (true);
		}
	}
	
	public final void statement_list() throws RecognitionException, TokenStreamException {
		
		
		{
		int _cnt306=0;
		_loop306:
		do {
			if ((_tokenSet_59.member(LA(1)))) {
				statement();
			}
			else {
				if ( _cnt306>=1 ) { break _loop306; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt306++;
		} while (true);
		}
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case LITERAL_case:
		{
			case_statement();
			break;
		}
		case LITERAL_default:
		{
			default_statement();
			break;
		}
		case LCURLY:
		{
			compound_statement();
			break;
		}
		case LITERAL_if:
		case LITERAL_switch:
		{
			selection_statement();
			break;
		}
		case LITERAL_while:
		case LITERAL_do:
		case LITERAL_for:
		{
			iteration_statement();
			break;
		}
		case LITERAL_goto:
		case LITERAL_continue:
		case LITERAL_break:
		case LITERAL_return:
		{
			jump_statement();
			break;
		}
		case SEMICOLON:
		{
			match(SEMICOLON);
			break;
		}
		case LITERAL_try:
		{
			try_block();
			break;
		}
		case LITERAL_throw:
		{
			throw_statement();
			break;
		}
		case LITERAL__asm:
		case LITERAL___asm:
		{
			asm_block();
			break;
		}
		default:
			boolean synPredMatched310 = false;
			if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
				int _m310 = mark();
				synPredMatched310 = true;
				inputState.guessing++;
				try {
					{
					declaration();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched310 = false;
				}
				rewind(_m310);
inputState.guessing--;
			}
			if ( synPredMatched310 ) {
				declaration();
			}
			else if ((LA(1)==ID) && (LA(2)==COLON)) {
				labeled_statement();
			}
			else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_60.member(LA(2)))) {
				expression();
				match(SEMICOLON);
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void labeled_statement() throws RecognitionException, TokenStreamException {
		
		
		match(ID);
		match(COLON);
		statement();
	}
	
	public final void case_statement() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_case);
		constant_expression();
		match(COLON);
		statement();
	}
	
	public final void default_statement() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_default);
		match(COLON);
		statement();
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		
		assignment_expression();
		{
		_loop345:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				assignment_expression();
			}
			else {
				break _loop345;
			}
			
		} while (true);
		}
	}
	
	public final void selection_statement() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_if:
		{
			if ( inputState.guessing==0 ) {
				b.beginIfStatement();
			}
			match(LITERAL_if);
			match(LPAREN);
			expression();
			match(RPAREN);
			statement();
			{
			if ((LA(1)==LITERAL_else) && (_tokenSet_59.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					b.beginElseStatement();
				}
				match(LITERAL_else);
				statement();
			}
			else if ((_tokenSet_61.member(LA(1))) && (_tokenSet_62.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			if ( inputState.guessing==0 ) {
				b.endIfElseStatement();
			}
			break;
		}
		case LITERAL_switch:
		{
			match(LITERAL_switch);
			match(LPAREN);
			expression();
			match(RPAREN);
			statement();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void iteration_statement() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_while:
		{
			if ( inputState.guessing==0 ) {
				b.beginWhileStatement();
			}
			match(LITERAL_while);
			match(LPAREN);
			expression();
			match(RPAREN);
			statement();
			if ( inputState.guessing==0 ) {
				b.endWhileStatement();
			}
			break;
		}
		case LITERAL_do:
		{
			if ( inputState.guessing==0 ) {
				b.beginDoStatement();
			}
			match(LITERAL_do);
			statement();
			match(LITERAL_while);
			match(LPAREN);
			expression();
			match(RPAREN);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.endDoStatement();
			}
			break;
		}
		case LITERAL_for:
		{
			if ( inputState.guessing==0 ) {
				b.beginForStatement();
			}
			match(LITERAL_for);
			match(LPAREN);
			{
			boolean synPredMatched321 = false;
			if (((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))))) {
				int _m321 = mark();
				synPredMatched321 = true;
				inputState.guessing++;
				try {
					{
					declaration();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched321 = false;
				}
				rewind(_m321);
inputState.guessing--;
			}
			if ( synPredMatched321 ) {
				declaration();
			}
			else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_60.member(LA(2)))) {
				expression();
				match(SEMICOLON);
			}
			else if ((LA(1)==SEMICOLON)) {
				match(SEMICOLON);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			switch ( LA(1)) {
			case ID:
			case StringLiteral:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_const_cast:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case OCTALINT:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case PLUS:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LITERAL_sizeof:
			case SCOPE:
			case LITERAL_dynamic_cast:
			case LITERAL_static_cast:
			case LITERAL_reinterpret_cast:
			case NOT:
			case LITERAL_new:
			case LITERAL_delete:
			case DECIMALINT:
			case HEXADECIMALINT:
			case CharLiteral:
			case FLOATONE:
			case FLOATTWO:
			{
				expression();
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(SEMICOLON);
			{
			switch ( LA(1)) {
			case ID:
			case StringLiteral:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_const_cast:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case OCTALINT:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case PLUS:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LITERAL_sizeof:
			case SCOPE:
			case LITERAL_dynamic_cast:
			case LITERAL_static_cast:
			case LITERAL_reinterpret_cast:
			case NOT:
			case LITERAL_new:
			case LITERAL_delete:
			case DECIMALINT:
			case HEXADECIMALINT:
			case CharLiteral:
			case FLOATONE:
			case FLOATTWO:
			{
				expression();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			statement();
			if ( inputState.guessing==0 ) {
				b.endForStatement();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void jump_statement() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case LITERAL_goto:
		{
			match(LITERAL_goto);
			match(ID);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.gotoStatement();
			}
			break;
		}
		case LITERAL_continue:
		{
			match(LITERAL_continue);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.continueStatement();
			}
			break;
		}
		case LITERAL_break:
		{
			match(LITERAL_break);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.breakStatement();
			}
			break;
		}
		case LITERAL_return:
		{
			match(LITERAL_return);
			{
			boolean synPredMatched328 = false;
			if (((LA(1)==LPAREN) && (LA(2)==ID))) {
				int _m328 = mark();
				synPredMatched328 = true;
				inputState.guessing++;
				try {
					{
					match(LPAREN);
					if (!((qualifiedItemIsOneOf(CPPvariables.QI_TYPE,0) )))
					  throw new SemanticException("(qualifiedItemIsOneOf(CPPvariables.QI_TYPE,0) )");
					match(ID);
					match(RPAREN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched328 = false;
				}
				rewind(_m328);
inputState.guessing--;
			}
			if ( synPredMatched328 ) {
				match(LPAREN);
				match(ID);
				match(RPAREN);
				{
				switch ( LA(1)) {
				case ID:
				case StringLiteral:
				case LITERAL__declspec:
				case LITERAL___declspec:
				case LPAREN:
				case LITERAL_const_cast:
				case LITERAL_char:
				case LITERAL_wchar_t:
				case LITERAL_bool:
				case LITERAL_short:
				case LITERAL_int:
				case 44:
				case 45:
				case 46:
				case LITERAL_long:
				case LITERAL_signed:
				case LITERAL_unsigned:
				case LITERAL_float:
				case LITERAL_double:
				case LITERAL_void:
				case OPERATOR:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_false:
				case OCTALINT:
				case STAR:
				case AMPERSAND:
				case TILDE:
				case PLUS:
				case MINUS:
				case PLUSPLUS:
				case MINUSMINUS:
				case LITERAL_sizeof:
				case SCOPE:
				case LITERAL_dynamic_cast:
				case LITERAL_static_cast:
				case LITERAL_reinterpret_cast:
				case NOT:
				case LITERAL_new:
				case LITERAL_delete:
				case DECIMALINT:
				case HEXADECIMALINT:
				case CharLiteral:
				case FLOATONE:
				case FLOATTWO:
				{
					expression();
					break;
				}
				case SEMICOLON:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_60.member(LA(2)))) {
				expression();
			}
			else if ((LA(1)==SEMICOLON)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				b.returnStatement();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void try_block() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_try);
		compound_statement();
		{
		_loop332:
		do {
			if ((LA(1)==LITERAL_catch)) {
				handler();
			}
			else {
				break _loop332;
			}
			
		} while (true);
		}
	}
	
	public final void throw_statement() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_throw);
		{
		switch ( LA(1)) {
		case ID:
		case StringLiteral:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_const_cast:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			assignment_expression();
			break;
		}
		case SEMICOLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMICOLON);
	}
	
	public final void asm_block() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case LITERAL__asm:
		{
			match(LITERAL__asm);
			break;
		}
		case LITERAL___asm:
		{
			match(LITERAL___asm);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LCURLY);
		{
		_loop342:
		do {
			if ((_tokenSet_63.member(LA(1)))) {
				matchNot(RCURLY);
			}
			else {
				break _loop342;
			}
			
		} while (true);
		}
		match(RCURLY);
	}
	
	public final void handler() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_catch);
		match(LPAREN);
		exception_declaration();
		match(RPAREN);
		compound_statement();
	}
	
	public final void exception_declaration() throws RecognitionException, TokenStreamException {
		
		
		parameter_declaration_list();
	}
	
	public final void assignment_expression() throws RecognitionException, TokenStreamException {
		
		
		conditional_expression();
		{
		switch ( LA(1)) {
		case ASSIGNEQUAL:
		case TIMESEQUAL:
		case DIVIDEEQUAL:
		case MINUSEQUAL:
		case PLUSEQUAL:
		case MODEQUAL:
		case SHIFTLEFTEQUAL:
		case SHIFTRIGHTEQUAL:
		case BITWISEANDEQUAL:
		case BITWISEXOREQUAL:
		case BITWISEOREQUAL:
		{
			{
			switch ( LA(1)) {
			case ASSIGNEQUAL:
			{
				match(ASSIGNEQUAL);
				break;
			}
			case TIMESEQUAL:
			{
				match(TIMESEQUAL);
				break;
			}
			case DIVIDEEQUAL:
			{
				match(DIVIDEEQUAL);
				break;
			}
			case MINUSEQUAL:
			{
				match(MINUSEQUAL);
				break;
			}
			case PLUSEQUAL:
			{
				match(PLUSEQUAL);
				break;
			}
			case MODEQUAL:
			{
				match(MODEQUAL);
				break;
			}
			case SHIFTLEFTEQUAL:
			{
				match(SHIFTLEFTEQUAL);
				break;
			}
			case SHIFTRIGHTEQUAL:
			{
				match(SHIFTRIGHTEQUAL);
				break;
			}
			case BITWISEANDEQUAL:
			{
				match(BITWISEANDEQUAL);
				break;
			}
			case BITWISEXOREQUAL:
			{
				match(BITWISEXOREQUAL);
				break;
			}
			case BITWISEOREQUAL:
			{
				match(BITWISEOREQUAL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			remainder_expression();
			break;
		}
		case GREATERTHAN:
		case SEMICOLON:
		case RCURLY:
		case COLON:
		case COMMA:
		case RPAREN:
		case RSQUARE:
		case ELLIPSIS:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void conditional_expression() throws RecognitionException, TokenStreamException {
		
		
		logical_or_expression();
		{
		switch ( LA(1)) {
		case QUESTIONMARK:
		{
			match(QUESTIONMARK);
			expression();
			match(COLON);
			conditional_expression();
			break;
		}
		case GREATERTHAN:
		case SEMICOLON:
		case RCURLY:
		case ASSIGNEQUAL:
		case COLON:
		case COMMA:
		case RPAREN:
		case RSQUARE:
		case ELLIPSIS:
		case TIMESEQUAL:
		case DIVIDEEQUAL:
		case MINUSEQUAL:
		case PLUSEQUAL:
		case MODEQUAL:
		case SHIFTLEFTEQUAL:
		case SHIFTRIGHTEQUAL:
		case BITWISEANDEQUAL:
		case BITWISEXOREQUAL:
		case BITWISEOREQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void logical_or_expression() throws RecognitionException, TokenStreamException {
		
		
		logical_and_expression();
		{
		_loop359:
		do {
			if ((LA(1)==OR)) {
				match(OR);
				logical_and_expression();
			}
			else {
				break _loop359;
			}
			
		} while (true);
		}
	}
	
	public final void logical_and_expression() throws RecognitionException, TokenStreamException {
		
		
		inclusive_or_expression();
		{
		_loop362:
		do {
			if ((LA(1)==AND)) {
				match(AND);
				inclusive_or_expression();
			}
			else {
				break _loop362;
			}
			
		} while (true);
		}
	}
	
	public final void inclusive_or_expression() throws RecognitionException, TokenStreamException {
		
		
		exclusive_or_expression();
		{
		_loop365:
		do {
			if ((LA(1)==BITWISEOR)) {
				match(BITWISEOR);
				exclusive_or_expression();
			}
			else {
				break _loop365;
			}
			
		} while (true);
		}
	}
	
	public final void exclusive_or_expression() throws RecognitionException, TokenStreamException {
		
		
		and_expression();
		{
		_loop368:
		do {
			if ((LA(1)==BITWISEXOR)) {
				match(BITWISEXOR);
				and_expression();
			}
			else {
				break _loop368;
			}
			
		} while (true);
		}
	}
	
	public final void and_expression() throws RecognitionException, TokenStreamException {
		
		
		equality_expression();
		{
		_loop371:
		do {
			if ((LA(1)==AMPERSAND)) {
				match(AMPERSAND);
				equality_expression();
			}
			else {
				break _loop371;
			}
			
		} while (true);
		}
	}
	
	public final void equality_expression() throws RecognitionException, TokenStreamException {
		
		
		relational_expression();
		{
		_loop375:
		do {
			if ((LA(1)==NOTEQUAL||LA(1)==EQUAL)) {
				{
				switch ( LA(1)) {
				case NOTEQUAL:
				{
					match(NOTEQUAL);
					break;
				}
				case EQUAL:
				{
					match(EQUAL);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				relational_expression();
			}
			else {
				break _loop375;
			}
			
		} while (true);
		}
	}
	
	public final void relational_expression() throws RecognitionException, TokenStreamException {
		
		
		shift_expression();
		{
		_loop379:
		do {
			if ((_tokenSet_64.member(LA(1))) && (_tokenSet_42.member(LA(2)))) {
				{
				switch ( LA(1)) {
				case LESSTHAN:
				{
					match(LESSTHAN);
					break;
				}
				case GREATERTHAN:
				{
					match(GREATERTHAN);
					break;
				}
				case LESSTHANOREQUALTO:
				{
					match(LESSTHANOREQUALTO);
					break;
				}
				case GREATERTHANOREQUALTO:
				{
					match(GREATERTHANOREQUALTO);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				shift_expression();
			}
			else {
				break _loop379;
			}
			
		} while (true);
		}
	}
	
	public final void additive_expression() throws RecognitionException, TokenStreamException {
		
		
		multiplicative_expression();
		{
		_loop387:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					match(PLUS);
					break;
				}
				case MINUS:
				{
					match(MINUS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				multiplicative_expression();
			}
			else {
				break _loop387;
			}
			
		} while (true);
		}
	}
	
	public final void multiplicative_expression() throws RecognitionException, TokenStreamException {
		
		
		pm_expression();
		{
		_loop391:
		do {
			if ((_tokenSet_65.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case STAR:
				{
					match(STAR);
					break;
				}
				case DIVIDE:
				{
					match(DIVIDE);
					break;
				}
				case MOD:
				{
					match(MOD);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				pm_expression();
			}
			else {
				break _loop391;
			}
			
		} while (true);
		}
	}
	
	public final void pm_expression() throws RecognitionException, TokenStreamException {
		
		
		cast_expression();
		{
		_loop395:
		do {
			if ((LA(1)==DOTMBR||LA(1)==POINTERTOMBR)) {
				{
				switch ( LA(1)) {
				case DOTMBR:
				{
					match(DOTMBR);
					break;
				}
				case POINTERTOMBR:
				{
					match(POINTERTOMBR);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				cast_expression();
			}
			else {
				break _loop395;
			}
			
		} while (true);
		}
	}
	
	public final void cast_expression() throws RecognitionException, TokenStreamException {
		
		
		boolean synPredMatched400 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_66.member(LA(2))))) {
			int _m400 = mark();
			synPredMatched400 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				{
				switch ( LA(1)) {
				case LITERAL_const:
				case LITERAL_const_cast:
				case LITERAL_volatile:
				{
					type_qualifier();
					break;
				}
				case ID:
				case LITERAL__declspec:
				case LITERAL___declspec:
				case LITERAL_char:
				case LITERAL_wchar_t:
				case LITERAL_bool:
				case LITERAL_short:
				case LITERAL_int:
				case 44:
				case 45:
				case 46:
				case LITERAL_long:
				case LITERAL_signed:
				case LITERAL_unsigned:
				case LITERAL_float:
				case LITERAL_double:
				case LITERAL_void:
				case SCOPE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				simple_type_specifier();
				{
				switch ( LA(1)) {
				case ID:
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				case STAR:
				case AMPERSAND:
				case SCOPE:
				case LITERAL__cdecl:
				case LITERAL___cdecl:
				case LITERAL__near:
				case LITERAL___near:
				case LITERAL__far:
				case LITERAL___far:
				case LITERAL___interrupt:
				case LITERAL_pascal:
				case LITERAL__pascal:
				case LITERAL___pascal:
				{
					ptr_operator();
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched400 = false;
			}
			rewind(_m400);
inputState.guessing--;
		}
		if ( synPredMatched400 ) {
			match(LPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_const:
			case LITERAL_const_cast:
			case LITERAL_volatile:
			{
				type_qualifier();
				break;
			}
			case ID:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case SCOPE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			simple_type_specifier();
			{
			switch ( LA(1)) {
			case ID:
			case LITERAL__stdcall:
			case LITERAL___stdcall:
			case STAR:
			case AMPERSAND:
			case SCOPE:
			case LITERAL__cdecl:
			case LITERAL___cdecl:
			case LITERAL__near:
			case LITERAL___near:
			case LITERAL__far:
			case LITERAL___far:
			case LITERAL___interrupt:
			case LITERAL_pascal:
			case LITERAL__pascal:
			case LITERAL___pascal:
			{
				ptr_operator();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			cast_expression();
		}
		else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_43.member(LA(2)))) {
			unary_expression();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void unary_expression() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case PLUSPLUS:
		{
			match(PLUSPLUS);
			unary_expression();
			break;
		}
		case MINUSMINUS:
		{
			match(MINUSMINUS);
			unary_expression();
			break;
		}
		case LITERAL_sizeof:
		{
			match(LITERAL_sizeof);
			{
			if (((LA(1)==LPAREN) && (_tokenSet_11.member(LA(2))))&&((!(((LA(1)==LPAREN&&(LA(2)==ID))))||(isTypeName(LT(2).getText()))))) {
				match(LPAREN);
				type_name();
				match(RPAREN);
			}
			else if ((_tokenSet_42.member(LA(1))) && (_tokenSet_43.member(LA(2)))) {
				unary_expression();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		default:
			boolean synPredMatched406 = false;
			if (((_tokenSet_67.member(LA(1))) && (_tokenSet_68.member(LA(2))))) {
				int _m406 = mark();
				synPredMatched406 = true;
				inputState.guessing++;
				try {
					{
					postfix_expression();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched406 = false;
				}
				rewind(_m406);
inputState.guessing--;
			}
			if ( synPredMatched406 ) {
				postfix_expression();
			}
			else if ((_tokenSet_69.member(LA(1))) && (_tokenSet_42.member(LA(2)))) {
				unary_operator();
				cast_expression();
			}
			else if ((_tokenSet_70.member(LA(1))) && (_tokenSet_71.member(LA(2)))) {
				{
				switch ( LA(1)) {
				case SCOPE:
				{
					match(SCOPE);
					break;
				}
				case LITERAL_new:
				case LITERAL_delete:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case LITERAL_new:
				{
					new_expression();
					break;
				}
				case LITERAL_delete:
				{
					delete_expression();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void postfix_expression() throws RecognitionException, TokenStreamException {
		
		
		{
		boolean synPredMatched413 = false;
		if ((((_tokenSet_55.member(LA(1))) && (_tokenSet_72.member(LA(2))))&&(!(LA(1)==LPAREN)))) {
			int _m413 = mark();
			synPredMatched413 = true;
			inputState.guessing++;
			try {
				{
				simple_type_specifier();
				match(LPAREN);
				match(RPAREN);
				match(LPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched413 = false;
			}
			rewind(_m413);
inputState.guessing--;
		}
		if ( synPredMatched413 ) {
			simple_type_specifier();
			match(LPAREN);
			match(RPAREN);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case ID:
			case StringLiteral:
			case LITERAL__declspec:
			case LITERAL___declspec:
			case LPAREN:
			case LITERAL_const_cast:
			case LITERAL_char:
			case LITERAL_wchar_t:
			case LITERAL_bool:
			case LITERAL_short:
			case LITERAL_int:
			case 44:
			case 45:
			case 46:
			case LITERAL_long:
			case LITERAL_signed:
			case LITERAL_unsigned:
			case LITERAL_float:
			case LITERAL_double:
			case LITERAL_void:
			case OPERATOR:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_false:
			case OCTALINT:
			case STAR:
			case AMPERSAND:
			case TILDE:
			case PLUS:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LITERAL_sizeof:
			case SCOPE:
			case LITERAL_dynamic_cast:
			case LITERAL_static_cast:
			case LITERAL_reinterpret_cast:
			case NOT:
			case LITERAL_new:
			case LITERAL_delete:
			case DECIMALINT:
			case HEXADECIMALINT:
			case CharLiteral:
			case FLOATONE:
			case FLOATTWO:
			{
				expression_list();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
		}
		else {
			boolean synPredMatched416 = false;
			if ((((_tokenSet_55.member(LA(1))) && (_tokenSet_72.member(LA(2))))&&(!(LA(1)==LPAREN)))) {
				int _m416 = mark();
				synPredMatched416 = true;
				inputState.guessing++;
				try {
					{
					simple_type_specifier();
					match(LPAREN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched416 = false;
				}
				rewind(_m416);
inputState.guessing--;
			}
			if ( synPredMatched416 ) {
				simple_type_specifier();
				match(LPAREN);
				{
				switch ( LA(1)) {
				case ID:
				case StringLiteral:
				case LITERAL__declspec:
				case LITERAL___declspec:
				case LPAREN:
				case LITERAL_const_cast:
				case LITERAL_char:
				case LITERAL_wchar_t:
				case LITERAL_bool:
				case LITERAL_short:
				case LITERAL_int:
				case 44:
				case 45:
				case 46:
				case LITERAL_long:
				case LITERAL_signed:
				case LITERAL_unsigned:
				case LITERAL_float:
				case LITERAL_double:
				case LITERAL_void:
				case OPERATOR:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_false:
				case OCTALINT:
				case STAR:
				case AMPERSAND:
				case TILDE:
				case PLUS:
				case MINUS:
				case PLUSPLUS:
				case MINUSMINUS:
				case LITERAL_sizeof:
				case SCOPE:
				case LITERAL_dynamic_cast:
				case LITERAL_static_cast:
				case LITERAL_reinterpret_cast:
				case NOT:
				case LITERAL_new:
				case LITERAL_delete:
				case DECIMALINT:
				case HEXADECIMALINT:
				case CharLiteral:
				case FLOATONE:
				case FLOATTWO:
				{
					expression_list();
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
			}
			else if ((_tokenSet_73.member(LA(1))) && (_tokenSet_68.member(LA(2)))) {
				primary_expression();
				{
				_loop420:
				do {
					switch ( LA(1)) {
					case LSQUARE:
					{
						match(LSQUARE);
						expression();
						match(RSQUARE);
						break;
					}
					case LPAREN:
					{
						match(LPAREN);
						{
						switch ( LA(1)) {
						case ID:
						case StringLiteral:
						case LITERAL__declspec:
						case LITERAL___declspec:
						case LPAREN:
						case LITERAL_const_cast:
						case LITERAL_char:
						case LITERAL_wchar_t:
						case LITERAL_bool:
						case LITERAL_short:
						case LITERAL_int:
						case 44:
						case 45:
						case 46:
						case LITERAL_long:
						case LITERAL_signed:
						case LITERAL_unsigned:
						case LITERAL_float:
						case LITERAL_double:
						case LITERAL_void:
						case OPERATOR:
						case LITERAL_this:
						case LITERAL_true:
						case LITERAL_false:
						case OCTALINT:
						case STAR:
						case AMPERSAND:
						case TILDE:
						case PLUS:
						case MINUS:
						case PLUSPLUS:
						case MINUSMINUS:
						case LITERAL_sizeof:
						case SCOPE:
						case LITERAL_dynamic_cast:
						case LITERAL_static_cast:
						case LITERAL_reinterpret_cast:
						case NOT:
						case LITERAL_new:
						case LITERAL_delete:
						case DECIMALINT:
						case HEXADECIMALINT:
						case CharLiteral:
						case FLOATONE:
						case FLOATTWO:
						{
							expression_list();
							break;
						}
						case RPAREN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(RPAREN);
						break;
					}
					case DOT:
					{
						match(DOT);
						id_expression();
						break;
					}
					case POINTERTO:
					{
						match(POINTERTO);
						id_expression();
						break;
					}
					case PLUSPLUS:
					{
						match(PLUSPLUS);
						break;
					}
					case MINUSMINUS:
					{
						match(MINUSMINUS);
						break;
					}
					default:
					{
						break _loop420;
					}
					}
				} while (true);
				}
			}
			else if ((_tokenSet_74.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case LITERAL_dynamic_cast:
				{
					match(LITERAL_dynamic_cast);
					break;
				}
				case LITERAL_static_cast:
				{
					match(LITERAL_static_cast);
					break;
				}
				case LITERAL_reinterpret_cast:
				{
					match(LITERAL_reinterpret_cast);
					break;
				}
				case LITERAL_const_cast:
				{
					match(LITERAL_const_cast);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(LESSTHAN);
				type_specifier();
				{
				switch ( LA(1)) {
				case ID:
				case LITERAL__stdcall:
				case LITERAL___stdcall:
				case STAR:
				case AMPERSAND:
				case SCOPE:
				case LITERAL__cdecl:
				case LITERAL___cdecl:
				case LITERAL__near:
				case LITERAL___near:
				case LITERAL__far:
				case LITERAL___far:
				case LITERAL___interrupt:
				case LITERAL_pascal:
				case LITERAL__pascal:
				case LITERAL___pascal:
				{
					ptr_operator();
					break;
				}
				case GREATERTHAN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(GREATERTHAN);
				match(LPAREN);
				expression();
				match(RPAREN);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		
	public final void unary_operator() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case AMPERSAND:
		{
			match(AMPERSAND);
			break;
		}
		case STAR:
		{
			match(STAR);
			break;
		}
		case PLUS:
		{
			match(PLUS);
			break;
		}
		case MINUS:
		{
			match(MINUS);
			break;
		}
		case TILDE:
		{
			match(TILDE);
			break;
		}
		case NOT:
		{
			match(NOT);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void new_expression() throws RecognitionException, TokenStreamException {
		
		
		{
		match(LITERAL_new);
		{
		boolean synPredMatched432 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_42.member(LA(2))))) {
			int _m432 = mark();
			synPredMatched432 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				expression_list();
				match(RPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched432 = false;
			}
			rewind(_m432);
inputState.guessing--;
		}
		if ( synPredMatched432 ) {
			match(LPAREN);
			expression_list();
			match(RPAREN);
		}
		else if ((_tokenSet_75.member(LA(1))) && (_tokenSet_76.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		switch ( LA(1)) {
		case LITERAL_typedef:
		case LITERAL_enum:
		case ID:
		case LITERAL_inline:
		case LITERAL_extern:
		case LITERAL__inline:
		case LITERAL___inline:
		case LITERAL_virtual:
		case LITERAL_explicit:
		case LITERAL_friend:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LITERAL_typename:
		case LITERAL_auto:
		case LITERAL_register:
		case LITERAL_static:
		case LITERAL_mutable:
		case LITERAL_const:
		case LITERAL_const_cast:
		case LITERAL_volatile:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case LITERAL_class:
		case LITERAL_struct:
		case LITERAL_union:
		case SCOPE:
		{
			new_type_id();
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			type_name();
			match(RPAREN);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			new_initializer();
			break;
		}
		case LESSTHAN:
		case GREATERTHAN:
		case SEMICOLON:
		case RCURLY:
		case ASSIGNEQUAL:
		case COLON:
		case COMMA:
		case RPAREN:
		case STAR:
		case AMPERSAND:
		case RSQUARE:
		case ELLIPSIS:
		case TIMESEQUAL:
		case DIVIDEEQUAL:
		case MINUSEQUAL:
		case PLUSEQUAL:
		case MODEQUAL:
		case SHIFTLEFTEQUAL:
		case SHIFTRIGHTEQUAL:
		case BITWISEANDEQUAL:
		case BITWISEXOREQUAL:
		case BITWISEOREQUAL:
		case QUESTIONMARK:
		case OR:
		case AND:
		case BITWISEOR:
		case BITWISEXOR:
		case NOTEQUAL:
		case EQUAL:
		case LESSTHANOREQUALTO:
		case GREATERTHANOREQUALTO:
		case SHIFTLEFT:
		case SHIFTRIGHT:
		case PLUS:
		case MINUS:
		case DIVIDE:
		case MOD:
		case DOTMBR:
		case POINTERTOMBR:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		}
	}
	
	public final void delete_expression() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_delete);
		{
		switch ( LA(1)) {
		case LSQUARE:
		{
			match(LSQUARE);
			match(RSQUARE);
			break;
		}
		case ID:
		case StringLiteral:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_const_cast:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		cast_expression();
	}
	
	public final void primary_expression() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case ID:
		case OPERATOR:
		case TILDE:
		case SCOPE:
		{
			id_expression();
			break;
		}
		case StringLiteral:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			constant();
			break;
		}
		case LITERAL_this:
		{
			match(LITERAL_this);
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			expression();
			match(RPAREN);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void id_expression() throws RecognitionException, TokenStreamException {
		
		String s="";
		
		s=scope_override();
		{
		switch ( LA(1)) {
		case ID:
		{
			match(ID);
			break;
		}
		case OPERATOR:
		{
			match(OPERATOR);
			optor();
			break;
		}
		case TILDE:
		{
			match(TILDE);
			{
			switch ( LA(1)) {
			case STAR:
			{
				match(STAR);
				break;
			}
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(ID);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case OCTALINT:
		{
			match(OCTALINT);
			break;
		}
		case DECIMALINT:
		{
			match(DECIMALINT);
			break;
		}
		case HEXADECIMALINT:
		{
			match(HEXADECIMALINT);
			break;
		}
		case CharLiteral:
		{
			match(CharLiteral);
			break;
		}
		case StringLiteral:
		{
			{
			int _cnt466=0;
			_loop466:
			do {
				if ((LA(1)==StringLiteral)) {
					match(StringLiteral);
				}
				else {
					if ( _cnt466>=1 ) { break _loop466; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt466++;
			} while (true);
			}
			break;
		}
		case FLOATONE:
		{
			match(FLOATONE);
			break;
		}
		case FLOATTWO:
		{
			match(FLOATTWO);
			break;
		}
		case LITERAL_true:
		{
			match(LITERAL_true);
			break;
		}
		case LITERAL_false:
		{
			match(LITERAL_false);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void new_type_id() throws RecognitionException, TokenStreamException {
		
		
		declaration_specifiers();
		{
		if ((_tokenSet_77.member(LA(1))) && (_tokenSet_78.member(LA(2)))) {
			new_declarator();
		}
		else if ((_tokenSet_79.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void new_initializer() throws RecognitionException, TokenStreamException {
		
		
		match(LPAREN);
		{
		switch ( LA(1)) {
		case ID:
		case StringLiteral:
		case LITERAL__declspec:
		case LITERAL___declspec:
		case LPAREN:
		case LITERAL_const_cast:
		case LITERAL_char:
		case LITERAL_wchar_t:
		case LITERAL_bool:
		case LITERAL_short:
		case LITERAL_int:
		case 44:
		case 45:
		case 46:
		case LITERAL_long:
		case LITERAL_signed:
		case LITERAL_unsigned:
		case LITERAL_float:
		case LITERAL_double:
		case LITERAL_void:
		case OPERATOR:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_false:
		case OCTALINT:
		case STAR:
		case AMPERSAND:
		case TILDE:
		case PLUS:
		case MINUS:
		case PLUSPLUS:
		case MINUSMINUS:
		case LITERAL_sizeof:
		case SCOPE:
		case LITERAL_dynamic_cast:
		case LITERAL_static_cast:
		case LITERAL_reinterpret_cast:
		case NOT:
		case LITERAL_new:
		case LITERAL_delete:
		case DECIMALINT:
		case HEXADECIMALINT:
		case CharLiteral:
		case FLOATONE:
		case FLOATTWO:
		{
			expression_list();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
	}
	
	public final void new_declarator() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case ID:
		case LITERAL__stdcall:
		case LITERAL___stdcall:
		case STAR:
		case AMPERSAND:
		case SCOPE:
		case LITERAL__cdecl:
		case LITERAL___cdecl:
		case LITERAL__near:
		case LITERAL___near:
		case LITERAL__far:
		case LITERAL___far:
		case LITERAL___interrupt:
		case LITERAL_pascal:
		case LITERAL__pascal:
		case LITERAL___pascal:
		{
			ptr_operator();
			{
			if ((_tokenSet_77.member(LA(1))) && (_tokenSet_78.member(LA(2)))) {
				new_declarator();
			}
			else if ((_tokenSet_79.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		case LSQUARE:
		{
			direct_new_declarator();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void direct_new_declarator() throws RecognitionException, TokenStreamException {
		
		
		{
		int _cnt458=0;
		_loop458:
		do {
			if ((LA(1)==LSQUARE)) {
				match(LSQUARE);
				expression();
				match(RSQUARE);
			}
			else {
				if ( _cnt458>=1 ) { break _loop458; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt458++;
		} while (true);
		}
	}
	
	public final void ptr_to_member() throws RecognitionException, TokenStreamException {
		
		String s="";
		
		s=scope_override();
		match(STAR);
		if ( inputState.guessing==0 ) {
			
			if (s.length() != 0) b.ptrToMember(s, "*");
			else b.ptrOperator("*");
			
		}
		cv_qualifier_seq();
	}
	
	public final void optor_simple_tokclass() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case PLUS:
		{
			match(PLUS);
			break;
		}
		case MINUS:
		{
			match(MINUS);
			break;
		}
		case STAR:
		{
			match(STAR);
			break;
		}
		case DIVIDE:
		{
			match(DIVIDE);
			break;
		}
		case MOD:
		{
			match(MOD);
			break;
		}
		case BITWISEXOR:
		{
			match(BITWISEXOR);
			break;
		}
		case AMPERSAND:
		{
			match(AMPERSAND);
			break;
		}
		case BITWISEOR:
		{
			match(BITWISEOR);
			break;
		}
		case TILDE:
		{
			match(TILDE);
			break;
		}
		case NOT:
		{
			match(NOT);
			break;
		}
		case SHIFTLEFT:
		{
			match(SHIFTLEFT);
			break;
		}
		case SHIFTRIGHT:
		{
			match(SHIFTRIGHT);
			break;
		}
		case ASSIGNEQUAL:
		{
			match(ASSIGNEQUAL);
			break;
		}
		case TIMESEQUAL:
		{
			match(TIMESEQUAL);
			break;
		}
		case DIVIDEEQUAL:
		{
			match(DIVIDEEQUAL);
			break;
		}
		case MODEQUAL:
		{
			match(MODEQUAL);
			break;
		}
		case PLUSEQUAL:
		{
			match(PLUSEQUAL);
			break;
		}
		case MINUSEQUAL:
		{
			match(MINUSEQUAL);
			break;
		}
		case SHIFTLEFTEQUAL:
		{
			match(SHIFTLEFTEQUAL);
			break;
		}
		case SHIFTRIGHTEQUAL:
		{
			match(SHIFTRIGHTEQUAL);
			break;
		}
		case BITWISEANDEQUAL:
		{
			match(BITWISEANDEQUAL);
			break;
		}
		case BITWISEXOREQUAL:
		{
			match(BITWISEXOREQUAL);
			break;
		}
		case BITWISEOREQUAL:
		{
			match(BITWISEOREQUAL);
			break;
		}
		case EQUAL:
		{
			match(EQUAL);
			break;
		}
		case NOTEQUAL:
		{
			match(NOTEQUAL);
			break;
		}
		case LESSTHAN:
		{
			match(LESSTHAN);
			break;
		}
		case GREATERTHAN:
		{
			match(GREATERTHAN);
			break;
		}
		case LESSTHANOREQUALTO:
		{
			match(LESSTHANOREQUALTO);
			break;
		}
		case GREATERTHANOREQUALTO:
		{
			match(GREATERTHANOREQUALTO);
			break;
		}
		case OR:
		{
			match(OR);
			break;
		}
		case AND:
		{
			match(AND);
			break;
		}
		case PLUSPLUS:
		{
			match(PLUSPLUS);
			break;
		}
		case MINUSMINUS:
		{
			match(MINUSMINUS);
			break;
		}
		case COMMA:
		{
			match(COMMA);
			break;
		}
		case POINTERTO:
		{
			match(POINTERTO);
			break;
		}
		case POINTERTOMBR:
		{
			match(POINTERTOMBR);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"template\"",
		"LESSTHAN",
		"GREATERTHAN",
		"\"typedef\"",
		"\"enum\"",
		"ID",
		"LCURLY",
		"SEMICOLON",
		"\"inline\"",
		"\"namespace\"",
		"RCURLY",
		"ASSIGNEQUAL",
		"COLON",
		"\"extern\"",
		"StringLiteral",
		"COMMA",
		"\"_inline\"",
		"\"__inline\"",
		"\"virtual\"",
		"\"explicit\"",
		"\"friend\"",
		"\"_stdcall\"",
		"\"__stdcall\"",
		"\"_declspec\"",
		"\"__declspec\"",
		"LPAREN",
		"RPAREN",
		"\"typename\"",
		"\"auto\"",
		"\"register\"",
		"\"static\"",
		"\"mutable\"",
		"\"const\"",
		"\"const_cast\"",
		"\"volatile\"",
		"\"char\"",
		"\"wchar_t\"",
		"\"bool\"",
		"\"short\"",
		"\"int\"",
		"\"_int64\"",
		"\"__int64\"",
		"\"__w64\"",
		"\"long\"",
		"\"signed\"",
		"\"unsigned\"",
		"\"float\"",
		"\"double\"",
		"\"void\"",
		"\"class\"",
		"\"struct\"",
		"\"union\"",
		"\"operator\"",
		"\"this\"",
		"\"true\"",
		"\"false\"",
		"\"public\"",
		"\"protected\"",
		"\"private\"",
		"OCTALINT",
		"STAR",
		"AMPERSAND",
		"LSQUARE",
		"RSQUARE",
		"TILDE",
		"ELLIPSIS",
		"\"throw\"",
		"\"case\"",
		"\"default\"",
		"\"if\"",
		"\"else\"",
		"\"switch\"",
		"\"while\"",
		"\"do\"",
		"\"for\"",
		"\"goto\"",
		"\"continue\"",
		"\"break\"",
		"\"return\"",
		"\"try\"",
		"\"catch\"",
		"\"using\"",
		"\"_asm\"",
		"\"__asm\"",
		"TIMESEQUAL",
		"DIVIDEEQUAL",
		"MINUSEQUAL",
		"PLUSEQUAL",
		"MODEQUAL",
		"SHIFTLEFTEQUAL",
		"SHIFTRIGHTEQUAL",
		"BITWISEANDEQUAL",
		"BITWISEXOREQUAL",
		"BITWISEOREQUAL",
		"QUESTIONMARK",
		"OR",
		"AND",
		"BITWISEOR",
		"BITWISEXOR",
		"NOTEQUAL",
		"EQUAL",
		"LESSTHANOREQUALTO",
		"GREATERTHANOREQUALTO",
		"SHIFTLEFT",
		"SHIFTRIGHT",
		"PLUS",
		"MINUS",
		"DIVIDE",
		"MOD",
		"DOTMBR",
		"POINTERTOMBR",
		"PLUSPLUS",
		"MINUSMINUS",
		"\"sizeof\"",
		"SCOPE",
		"DOT",
		"POINTERTO",
		"\"dynamic_cast\"",
		"\"static_cast\"",
		"\"reinterpret_cast\"",
		"NOT",
		"\"new\"",
		"\"_cdecl\"",
		"\"__cdecl\"",
		"\"_near\"",
		"\"__near\"",
		"\"_far\"",
		"\"__far\"",
		"\"__interrupt\"",
		"\"pascal\"",
		"\"_pascal\"",
		"\"__pascal\"",
		"\"delete\"",
		"DECIMALINT",
		"HEXADECIMALINT",
		"CharLiteral",
		"FLOATONE",
		"FLOATTWO",
		"Whitespace",
		"Comment",
		"CPPComment",
		"DIRECTIVE",
		"LineDirective",
		"EndOfLine",
		"Escape",
		"Digit",
		"Decimal",
		"LongSuffix",
		"UnsignedSuffix",
		"FloatSuffix",
		"Exponent",
		"Vocabulary",
		"Number"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 1152921503532202896L, -4593671619915808749L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 72057592426402688L, 18014398511579136L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1152921501385506720L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 7344656L, 18014398509482000L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 7344672L, 18014398509482000L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 11538944L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 548409888L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 72057594037932544L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 144115186464330656L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 1152921503532192640L, -4593671619917905917L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 1152921501385267168L, -1054405279954763753L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 72057592426402688L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1152921501385236384L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 100663808L, -4593671619917905917L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 1080864392242790944L, -4593671619917905917L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 1080863911105790464L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 1080863911106347616L, 3557280738472624151L, 256L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 1152921501384710048L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 72057592426403712L, 18014398511579136L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { -8070450533321769056L, -110232637738583085L, 16383L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { -8070450533322301568L, 4501453380688804819L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 1080863911206453760L, -4593671619917905917L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 1080864392243348064L, -1054405279954763753L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 1152921503532220304L, -4593671619915808749L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { -8070450533322321024L, 4501453380673077267L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 1080863912280837728L, -4591419820120997825L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { -14L, -1048577L, 16383L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 7344640L, 18014398509482000L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 144115186464330624L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 1080863910568919552L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 1080863911106349664L, 3557280738472624151L, 256L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 1152921501384777632L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { -8133501338408189440L, 4501453380673077275L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { 1080863912280836704L, -1053842312821473217L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { 9006649901580288L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 9223372035780139920L, -4593671619915808749L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = { 9223372035780123536L, -4593671619915808749L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = { 1080863912280836704L, -4591419820120997825L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = { 1080863912280837728L, -4476578029623050177L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = { 1080864392242790944L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = { 1080863911105790464L, 18014398509482000L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = { 1080863912280754784L, -1054405279954763721L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = { -8133501338408189440L, 4501453380673077267L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = { -8070450532247938080L, 4611686018410610751L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = { -8133501338408123904L, 4501453380673077267L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = { 1080863911206453760L, -4593671619917905901L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = { 1080864392243350112L, -1054405279954763753L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = { 72057592426438528L, 18014398511579200L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = { 1152921502459008992L, -4593671619917905865L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = { 1080864393317089888L, -1054405279954763721L, 511L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	private static final long[] mk_tokenSet_50() {
		long[] data = { 1711833664L, -4593671619917905881L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	private static final long[] mk_tokenSet_51() {
		long[] data = { -8070450532247937056L, -108086391071571841L, 16383L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_51 = new BitSet(mk_tokenSet_51());
	private static final long[] mk_tokenSet_52() {
		long[] data = { 524864L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_52 = new BitSet(mk_tokenSet_52());
	private static final long[] mk_tokenSet_53() {
		long[] data = { 1152921503532192640L, -4593671619917905869L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_53 = new BitSet(mk_tokenSet_53());
	private static final long[] mk_tokenSet_54() {
		long[] data = { 638059104L, -4593671619917905913L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_54 = new BitSet(mk_tokenSet_54());
	private static final long[] mk_tokenSet_55() {
		long[] data = { 9006649901580800L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_55 = new BitSet(mk_tokenSet_55());
	private static final long[] mk_tokenSet_56() {
		long[] data = { 9006650539639392L, -4593671619917905913L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_56 = new BitSet(mk_tokenSet_56());
	private static final long[] mk_tokenSet_57() {
		long[] data = { 1152921501385234400L, -4593671619917905897L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_57 = new BitSet(mk_tokenSet_57());
	private static final long[] mk_tokenSet_58() {
		long[] data = { -8070450533321763872L, 4611686001230741527L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_58 = new BitSet(mk_tokenSet_58());
	private static final long[] mk_tokenSet_59() {
		long[] data = { -8070450533322317952L, 4501453380688804819L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_59 = new BitSet(mk_tokenSet_59());
	private static final long[] mk_tokenSet_60() {
		long[] data = { -8070450533321761824L, 4611686018410610711L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_60 = new BitSet(mk_tokenSet_60());
	private static final long[] mk_tokenSet_61() {
		long[] data = { -8070450533322301568L, 4501453380688805843L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_61 = new BitSet(mk_tokenSet_61());
	private static final long[] mk_tokenSet_62() {
		long[] data = { -1073741838L, -41L, 16383L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_62 = new BitSet(mk_tokenSet_62());
	private static final long[] mk_tokenSet_63() {
		long[] data = { -16400L, -1L, 536870911L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_63 = new BitSet(mk_tokenSet_63());
	private static final long[] mk_tokenSet_64() {
		long[] data = { 96L, 6597069766656L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_64 = new BitSet(mk_tokenSet_64());
	private static final long[] mk_tokenSet_65() {
		long[] data = { 0L, 422212465065985L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_65 = new BitSet(mk_tokenSet_65());
	private static final long[] mk_tokenSet_66() {
		long[] data = { 9007130937917952L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_66 = new BitSet(mk_tokenSet_66());
	private static final long[] mk_tokenSet_67() {
		long[] data = { -8133501338408189440L, 1026820715040473104L, 15872L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_67 = new BitSet(mk_tokenSet_67());
	private static final long[] mk_tokenSet_68() {
		long[] data = { -8133501337333806496L, 4611686018410610751L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_68 = new BitSet(mk_tokenSet_68());
	private static final long[] mk_tokenSet_69() {
		long[] data = { 0L, 1153027057723113491L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_69 = new BitSet(mk_tokenSet_69());
	private static final long[] mk_tokenSet_70() {
		long[] data = { 0L, 2323857407723175936L, 256L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_70 = new BitSet(mk_tokenSet_70());
	private static final long[] mk_tokenSet_71() {
		long[] data = { -8070450533322321024L, 4501453380673077271L, 16128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_71 = new BitSet(mk_tokenSet_71());
	private static final long[] mk_tokenSet_72() {
		long[] data = { 9006650438451744L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_72 = new BitSet(mk_tokenSet_72());
	private static final long[] mk_tokenSet_73() {
		long[] data = { -8142508125748723200L, 18014398509482000L, 15872L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_73 = new BitSet(mk_tokenSet_73());
	private static final long[] mk_tokenSet_74() {
		long[] data = { 137438953472L, 1008806316530991104L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_74 = new BitSet(mk_tokenSet_74());
	private static final long[] mk_tokenSet_75() {
		long[] data = { 72057592963273600L, 18014398509481984L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_75 = new BitSet(mk_tokenSet_75());
	private static final long[] mk_tokenSet_76() {
		long[] data = { 1152921504606576608L, -4591419820120997825L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_76 = new BitSet(mk_tokenSet_76());
	private static final long[] mk_tokenSet_77() {
		long[] data = { 100663808L, -4593671619917905913L, 255L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_77 = new BitSet(mk_tokenSet_77());
	private static final long[] mk_tokenSet_78() {
		long[] data = { -8133500993635759520L, -108086391073669057L, 16383L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_78 = new BitSet(mk_tokenSet_78());
	private static final long[] mk_tokenSet_79() {
		long[] data = { 1611253856L, 2251799796908075L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_79 = new BitSet(mk_tokenSet_79());
	
	}
