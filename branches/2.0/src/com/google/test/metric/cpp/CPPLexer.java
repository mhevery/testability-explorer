// $ANTLR 2.7.7 (2006-11-01): "cppparser.g" -> "CPPLexer.java"$

/*REMOVE_BEGIN*/
package com.google.test.metric.cpp;
/*REMOVE_END*/

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class CPPLexer extends antlr.CharScanner implements STDCTokenTypes, TokenStream
 {
public CPPLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public CPPLexer(Reader in) {
	this(new CharBuffer(in));
}
public CPPLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public CPPLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("_cdecl", this), new Integer(126));
	literals.put(new ANTLRHashString("__stdcall", this), new Integer(26));
	literals.put(new ANTLRHashString("extern", this), new Integer(17));
	literals.put(new ANTLRHashString("public", this), new Integer(60));
	literals.put(new ANTLRHashString("namespace", this), new Integer(13));
	literals.put(new ANTLRHashString("case", this), new Integer(71));
	literals.put(new ANTLRHashString("short", this), new Integer(42));
	literals.put(new ANTLRHashString("break", this), new Integer(81));
	literals.put(new ANTLRHashString("while", this), new Integer(76));
	literals.put(new ANTLRHashString("delete", this), new Integer(136));
	literals.put(new ANTLRHashString("new", this), new Integer(125));
	literals.put(new ANTLRHashString("__declspec", this), new Integer(28));
	literals.put(new ANTLRHashString("__pascal", this), new Integer(135));
	literals.put(new ANTLRHashString("template", this), new Integer(4));
	literals.put(new ANTLRHashString("reinterpret_cast", this), new Integer(123));
	literals.put(new ANTLRHashString("inline", this), new Integer(12));
	literals.put(new ANTLRHashString("unsigned", this), new Integer(49));
	literals.put(new ANTLRHashString("const", this), new Integer(36));
	literals.put(new ANTLRHashString("float", this), new Integer(50));
	literals.put(new ANTLRHashString("static_cast", this), new Integer(122));
	literals.put(new ANTLRHashString("return", this), new Integer(82));
	literals.put(new ANTLRHashString("throw", this), new Integer(70));
	literals.put(new ANTLRHashString("typename", this), new Integer(31));
	literals.put(new ANTLRHashString("using", this), new Integer(85));
	literals.put(new ANTLRHashString("operator", this), new Integer(56));
	literals.put(new ANTLRHashString("__far", this), new Integer(131));
	literals.put(new ANTLRHashString("sizeof", this), new Integer(117));
	literals.put(new ANTLRHashString("protected", this), new Integer(61));
	literals.put(new ANTLRHashString("class", this), new Integer(53));
	literals.put(new ANTLRHashString("_stdcall", this), new Integer(25));
	literals.put(new ANTLRHashString("friend", this), new Integer(24));
	literals.put(new ANTLRHashString("do", this), new Integer(77));
	literals.put(new ANTLRHashString("__interrupt", this), new Integer(132));
	literals.put(new ANTLRHashString("_far", this), new Integer(130));
	literals.put(new ANTLRHashString("__cdecl", this), new Integer(127));
	literals.put(new ANTLRHashString("_int64", this), new Integer(44));
	literals.put(new ANTLRHashString("__asm", this), new Integer(87));
	literals.put(new ANTLRHashString("pascal", this), new Integer(133));
	literals.put(new ANTLRHashString("typedef", this), new Integer(7));
	literals.put(new ANTLRHashString("const_cast", this), new Integer(37));
	literals.put(new ANTLRHashString("explicit", this), new Integer(23));
	literals.put(new ANTLRHashString("_asm", this), new Integer(86));
	literals.put(new ANTLRHashString("if", this), new Integer(73));
	literals.put(new ANTLRHashString("double", this), new Integer(51));
	literals.put(new ANTLRHashString("volatile", this), new Integer(38));
	literals.put(new ANTLRHashString("catch", this), new Integer(84));
	literals.put(new ANTLRHashString("union", this), new Integer(55));
	literals.put(new ANTLRHashString("dynamic_cast", this), new Integer(121));
	literals.put(new ANTLRHashString("try", this), new Integer(83));
	literals.put(new ANTLRHashString("register", this), new Integer(33));
	literals.put(new ANTLRHashString("_inline", this), new Integer(20));
	literals.put(new ANTLRHashString("auto", this), new Integer(32));
	literals.put(new ANTLRHashString("goto", this), new Integer(79));
	literals.put(new ANTLRHashString("enum", this), new Integer(8));
	literals.put(new ANTLRHashString("int", this), new Integer(43));
	literals.put(new ANTLRHashString("for", this), new Integer(78));
	literals.put(new ANTLRHashString("char", this), new Integer(39));
	literals.put(new ANTLRHashString("__near", this), new Integer(129));
	literals.put(new ANTLRHashString("private", this), new Integer(62));
	literals.put(new ANTLRHashString("default", this), new Integer(72));
	literals.put(new ANTLRHashString("false", this), new Integer(59));
	literals.put(new ANTLRHashString("this", this), new Integer(57));
	literals.put(new ANTLRHashString("static", this), new Integer(34));
	literals.put(new ANTLRHashString("mutable", this), new Integer(35));
	literals.put(new ANTLRHashString("continue", this), new Integer(80));
	literals.put(new ANTLRHashString("bool", this), new Integer(41));
	literals.put(new ANTLRHashString("struct", this), new Integer(54));
	literals.put(new ANTLRHashString("_near", this), new Integer(128));
	literals.put(new ANTLRHashString("__int64", this), new Integer(45));
	literals.put(new ANTLRHashString("signed", this), new Integer(48));
	literals.put(new ANTLRHashString("else", this), new Integer(74));
	literals.put(new ANTLRHashString("_declspec", this), new Integer(27));
	literals.put(new ANTLRHashString("_pascal", this), new Integer(134));
	literals.put(new ANTLRHashString("__w64", this), new Integer(46));
	literals.put(new ANTLRHashString("void", this), new Integer(52));
	literals.put(new ANTLRHashString("wchar_t", this), new Integer(40));
	literals.put(new ANTLRHashString("switch", this), new Integer(75));
	literals.put(new ANTLRHashString("__inline", this), new Integer(21));
	literals.put(new ANTLRHashString("true", this), new Integer(58));
	literals.put(new ANTLRHashString("long", this), new Integer(47));
	literals.put(new ANTLRHashString("virtual", this), new Integer(22));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '?':
				{
					mQUESTIONMARK(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSEMICOLON(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mLSQUARE(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mRSQUARE(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mRCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '~':
				{
					mTILDE(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\u000c':  case '\r':
				case ' ':  case '\\':
				{
					mWhitespace(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mDIRECTIVE(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mCharLiteral(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mStringLiteral(true);
					theRetToken=_returnToken;
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '_':  case 'a':
				case 'b':  case 'c':  case 'd':  case 'e':
				case 'f':  case 'g':  case 'h':  case 'i':
				case 'j':  case 'k':  case 'l':  case 'm':
				case 'n':  case 'o':  case 'p':  case 'q':
				case 'r':  case 's':  case 't':  case 'u':
				case 'v':  case 'w':  case 'x':  case 'y':
				case 'z':
				{
					mID(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='>') && (LA(2)=='>') && (LA(3)=='=')) {
						mSHIFTRIGHTEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (LA(2)=='<') && (LA(3)=='=')) {
						mSHIFTLEFTEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (LA(2)=='>') && (LA(3)=='*')) {
						mPOINTERTOMBR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (LA(2)=='>') && (true)) {
						mPOINTERTO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='=') && (LA(2)=='=')) {
						mEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='!') && (LA(2)=='=')) {
						mNOTEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (LA(2)=='=')) {
						mLESSTHANOREQUALTO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (LA(2)=='=')) {
						mGREATERTHANOREQUALTO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='/') && (LA(2)=='=')) {
						mDIVIDEEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='+') && (LA(2)=='=')) {
						mPLUSEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='+') && (LA(2)=='+')) {
						mPLUSPLUS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (LA(2)=='=')) {
						mMINUSEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (LA(2)=='-')) {
						mMINUSMINUS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='*') && (LA(2)=='=')) {
						mTIMESEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='%') && (LA(2)=='=')) {
						mMODEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (LA(2)=='>') && (true)) {
						mSHIFTRIGHT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (LA(2)=='<') && (true)) {
						mSHIFTLEFT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='&') && (LA(2)=='&')) {
						mAND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='|') && (LA(2)=='|')) {
						mOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='&') && (LA(2)=='=')) {
						mBITWISEANDEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='|') && (LA(2)=='=')) {
						mBITWISEOREQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='^') && (LA(2)=='=')) {
						mBITWISEXOREQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='*')) {
						mDOTMBR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)==':') && (LA(2)==':')) {
						mSCOPE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='/') && (LA(2)=='*')) {
						mComment(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='/') && (LA(2)=='/')) {
						mCPPComment(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='=') && (true)) {
						mASSIGNEQUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)==':') && (true)) {
						mCOLON(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (true)) {
						mLESSTHAN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (true)) {
						mGREATERTHAN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='/') && (true)) {
						mDIVIDE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='+') && (true)) {
						mPLUS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (true)) {
						mMINUS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='*') && (true)) {
						mSTAR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='%') && (true)) {
						mMOD(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='!') && (true)) {
						mNOT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='&') && (true)) {
						mAMPERSAND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='|') && (true)) {
						mBITWISEOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='^') && (true)) {
						mBITWISEXOR(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_0.member(LA(1))) && (true)) {
						mNumber(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mASSIGNEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGNEQUAL;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COLON;
		int _saveIndex;
		
		match(':');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mQUESTIONMARK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUESTIONMARK;
		int _saveIndex;
		
		match('?');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEMICOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEMICOLON;
		int _saveIndex;
		
		match(';');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOINTERTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POINTERTO;
		int _saveIndex;
		
		match("->");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LPAREN;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RPAREN;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLSQUARE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LSQUARE;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRSQUARE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RSQUARE;
		int _saveIndex;
		
		match(']');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LCURLY;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RCURLY;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQUAL;
		int _saveIndex;
		
		match("==");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNOTEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NOTEQUAL;
		int _saveIndex;
		
		match("!=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLESSTHANOREQUALTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LESSTHANOREQUALTO;
		int _saveIndex;
		
		match("<=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLESSTHAN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LESSTHAN;
		int _saveIndex;
		
		match("<");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGREATERTHANOREQUALTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GREATERTHANOREQUALTO;
		int _saveIndex;
		
		match(">=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGREATERTHAN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GREATERTHAN;
		int _saveIndex;
		
		match(">");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIVIDE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIVIDE;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIVIDEEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIVIDEEQUAL;
		int _saveIndex;
		
		match("/=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUSEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUSEQUAL;
		int _saveIndex;
		
		match("+=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUSPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUSPLUS;
		int _saveIndex;
		
		match("++");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUSEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUSEQUAL;
		int _saveIndex;
		
		match("-=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUSMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUSMINUS;
		int _saveIndex;
		
		match("--");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTIMESEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIMESEQUAL;
		int _saveIndex;
		
		match("*=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MOD;
		int _saveIndex;
		
		match('%');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMODEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MODEQUAL;
		int _saveIndex;
		
		match("%=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSHIFTRIGHT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SHIFTRIGHT;
		int _saveIndex;
		
		match(">>");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSHIFTRIGHTEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SHIFTRIGHTEQUAL;
		int _saveIndex;
		
		match(">>=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSHIFTLEFT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SHIFTLEFT;
		int _saveIndex;
		
		match("<<");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSHIFTLEFTEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SHIFTLEFTEQUAL;
		int _saveIndex;
		
		match("<<=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AND;
		int _saveIndex;
		
		match("&&");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NOT;
		int _saveIndex;
		
		match('!');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OR;
		int _saveIndex;
		
		match("||");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAMPERSAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AMPERSAND;
		int _saveIndex;
		
		match('&');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBITWISEANDEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BITWISEANDEQUAL;
		int _saveIndex;
		
		match("&=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTILDE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TILDE;
		int _saveIndex;
		
		match('~');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBITWISEOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BITWISEOR;
		int _saveIndex;
		
		match('|');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBITWISEOREQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BITWISEOREQUAL;
		int _saveIndex;
		
		match("|=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBITWISEXOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BITWISEXOR;
		int _saveIndex;
		
		match('^');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBITWISEXOREQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BITWISEXOREQUAL;
		int _saveIndex;
		
		match("^=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOINTERTOMBR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POINTERTOMBR;
		int _saveIndex;
		
		match("->*");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOTMBR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOTMBR;
		int _saveIndex;
		
		match(".*");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSCOPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SCOPE;
		int _saveIndex;
		
		match("::");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWhitespace(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Whitespace;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '\t':  case '\u000c':  case ' ':
		{
			{
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '\u000c':
			{
				match('\f');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			break;
		}
		case '\n':  case '\r':
		{
			{
			if ((LA(1)=='\r') && (LA(2)=='\n')) {
				match("\r\n");
			}
			else if ((LA(1)=='\r') && (true)) {
				match('\r');
			}
			else if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			if ( inputState.guessing==0 ) {
				newline();
			}
			break;
		}
		case '\\':
		{
			{
			if ((LA(1)=='\\') && (LA(2)=='\r') && (LA(3)=='\n')) {
				match("\\\r\n");
			}
			else if ((LA(1)=='\\') && (LA(2)=='\r') && (true)) {
				match("\\\r");
			}
			else if ((LA(1)=='\\') && (LA(2)=='\n')) {
				match("\\\n");
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mComment(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Comment;
		int _saveIndex;
		
		match("/*");
		{
		_loop527:
		do {
			if (((LA(1)=='*') && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')) && ((LA(3) >= '\u0000' && LA(3) <= '\u00ff')))&&(LA(2) != '/')) {
				match('*');
			}
			else if ((LA(1)=='\n'||LA(1)=='\r')) {
				mEndOfLine(false);
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop527;
			}
			
		} while (true);
		}
		match("*/");
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mEndOfLine(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EndOfLine;
		int _saveIndex;
		
		{
		if ((LA(1)=='\r') && (LA(2)=='\n') && (true)) {
			match("\r\n");
		}
		else if ((LA(1)=='\r') && (true) && (true)) {
			match('\r');
		}
		else if ((LA(1)=='\n')) {
			match('\n');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCPPComment(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CPPComment;
		int _saveIndex;
		
		match("//");
		{
		_loop531:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				{
				match(_tokenSet_2);
				}
			}
			else {
				break _loop531;
			}
			
		} while (true);
		}
		mEndOfLine(false);
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIRECTIVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIRECTIVE;
		int _saveIndex;
		Token ld=null;
		
		match('#');
		mLineDirective(true);
		ld=_returnToken;
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;  newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLineDirective(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LineDirective;
		int _saveIndex;
		
		{
		_loop536:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				{
				match(_tokenSet_2);
				}
			}
			else {
				break _loop536;
			}
			
		} while (true);
		}
		mEndOfLine(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCharLiteral(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CharLiteral;
		int _saveIndex;
		
		match('\'');
		{
		if ((LA(1)=='\\') && (_tokenSet_3.member(LA(2))) && (_tokenSet_4.member(LA(3)))) {
			mEscape(false);
		}
		else if ((_tokenSet_5.member(LA(1))) && (LA(2)=='\'') && (true)) {
			{
			match(_tokenSet_5);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		match('\'');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mEscape(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Escape;
		int _saveIndex;
		
		match('\\');
		{
		switch ( LA(1)) {
		case 'a':
		{
			match('a');
			break;
		}
		case 'b':
		{
			match('b');
			break;
		}
		case 'f':
		{
			match('f');
			break;
		}
		case 'n':
		{
			match('n');
			break;
		}
		case 'r':
		{
			match('r');
			break;
		}
		case 't':
		{
			match('t');
			break;
		}
		case 'v':
		{
			match('v');
			break;
		}
		case '"':
		{
			match('"');
			break;
		}
		case '\'':
		{
			match('\'');
			break;
		}
		case '\\':
		{
			match('\\');
			break;
		}
		case '?':
		{
			match('?');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		{
			{
			matchRange('0','3');
			}
			{
			if (((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_2.member(LA(2))) && (true)) {
				mDigit(false);
				{
				if (((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_2.member(LA(2))) && (true)) {
					mDigit(false);
				}
				else if ((_tokenSet_2.member(LA(1))) && (true) && (true)) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
			}
			else if ((_tokenSet_2.member(LA(1))) && (true) && (true)) {
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			break;
		}
		case '4':  case '5':  case '6':  case '7':
		{
			{
			matchRange('4','7');
			}
			{
			if (((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_2.member(LA(2))) && (true)) {
				mDigit(false);
			}
			else if ((_tokenSet_2.member(LA(1))) && (true) && (true)) {
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			break;
		}
		case 'x':
		{
			match('x');
			{
			int _cnt555=0;
			_loop555:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_2.member(LA(2))) && (true)) {
					mDigit(false);
				}
				else if (((LA(1) >= 'a' && LA(1) <= 'f')) && (_tokenSet_2.member(LA(2))) && (true)) {
					matchRange('a','f');
				}
				else if (((LA(1) >= 'A' && LA(1) <= 'F')) && (_tokenSet_2.member(LA(2))) && (true)) {
					matchRange('A','F');
				}
				else {
					if ( _cnt555>=1 ) { break _loop555; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt555++;
			} while (true);
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mStringLiteral(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = StringLiteral;
		int _saveIndex;
		
		match('"');
		{
		_loop544:
		do {
			if ((LA(1)=='\\') && (_tokenSet_3.member(LA(2)))) {
				mEscape(false);
			}
			else if ((LA(1)=='\\') && (LA(2)=='\n'||LA(2)=='\r')) {
				{
				if ((LA(1)=='\\') && (LA(2)=='\r') && (LA(3)=='\n')) {
					match("\\\r\n");
				}
				else if ((LA(1)=='\\') && (LA(2)=='\r') && (_tokenSet_2.member(LA(3)))) {
					match("\\\r");
				}
				else if ((LA(1)=='\\') && (LA(2)=='\n')) {
					match("\\\n");
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((_tokenSet_6.member(LA(1)))) {
				{
				match(_tokenSet_6);
				}
			}
			else {
				break _loop544;
			}
			
		} while (true);
		}
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDigit(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Digit;
		int _saveIndex;
		
		matchRange('0','9');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDecimal(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Decimal;
		int _saveIndex;
		
		{
		int _cnt559=0;
		_loop559:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				matchRange('0','9');
			}
			else {
				if ( _cnt559>=1 ) { break _loop559; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt559++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLongSuffix(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LongSuffix;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'l':
		{
			match('l');
			break;
		}
		case 'L':
		{
			match('L');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mUnsignedSuffix(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UnsignedSuffix;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'u':
		{
			match('u');
			break;
		}
		case 'U':
		{
			match('U');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFloatSuffix(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FloatSuffix;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'f':
		{
			match('f');
			break;
		}
		case 'F':
		{
			match('F');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mExponent(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Exponent;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'e':
		{
			match('e');
			break;
		}
		case 'E':
		{
			match('E');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		switch ( LA(1)) {
		case '+':
		{
			match('+');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		int _cnt567=0;
		_loop567:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mDigit(false);
			}
			else {
				if ( _cnt567>=1 ) { break _loop567; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt567++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mVocabulary(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Vocabulary;
		int _saveIndex;
		
		matchRange('\3','\377');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNumber(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Number;
		int _saveIndex;
		
		boolean synPredMatched574 = false;
		if ((((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_7.member(LA(2))) && (true))) {
			int _m574 = mark();
			synPredMatched574 = true;
			inputState.guessing++;
			try {
				{
				{
				int _cnt572=0;
				_loop572:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDigit(false);
					}
					else {
						if ( _cnt572>=1 ) { break _loop572; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt572++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case '.':
				{
					match('.');
					break;
				}
				case 'e':
				{
					match('e');
					break;
				}
				case 'E':
				{
					match('E');
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched574 = false;
			}
			rewind(_m574);
inputState.guessing--;
		}
		if ( synPredMatched574 ) {
			{
			int _cnt576=0;
			_loop576:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mDigit(false);
				}
				else {
					if ( _cnt576>=1 ) { break _loop576; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt576++;
			} while (true);
			}
			{
			switch ( LA(1)) {
			case '.':
			{
				match('.');
				{
				_loop579:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDigit(false);
					}
					else {
						break _loop579;
					}
					
				} while (true);
				}
				{
				if ((LA(1)=='E'||LA(1)=='e')) {
					mExponent(false);
				}
				else {
				}
				
				}
				if ( inputState.guessing==0 ) {
					_ttype =  FLOATONE;
				}
				break;
			}
			case 'E':  case 'e':
			{
				mExponent(false);
				if ( inputState.guessing==0 ) {
					_ttype = FLOATTWO;
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 'F':  case 'f':
			{
				mFloatSuffix(false);
				break;
			}
			case 'L':  case 'l':
			{
				mLongSuffix(false);
				break;
			}
			default:
				{
				}
			}
			}
		}
		else {
			boolean synPredMatched583 = false;
			if (((LA(1)=='.') && (LA(2)=='.'))) {
				int _m583 = mark();
				synPredMatched583 = true;
				inputState.guessing++;
				try {
					{
					match("...");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched583 = false;
				}
				rewind(_m583);
inputState.guessing--;
			}
			if ( synPredMatched583 ) {
				match("...");
				if ( inputState.guessing==0 ) {
					_ttype = ELLIPSIS;
				}
			}
			else if ((LA(1)=='0') && (LA(2)=='X'||LA(2)=='x')) {
				match('0');
				{
				switch ( LA(1)) {
				case 'x':
				{
					match('x');
					break;
				}
				case 'X':
				{
					match('X');
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				{
				int _cnt599=0;
				_loop599:
				do {
					switch ( LA(1)) {
					case 'a':  case 'b':  case 'c':  case 'd':
					case 'e':  case 'f':
					{
						matchRange('a','f');
						break;
					}
					case 'A':  case 'B':  case 'C':  case 'D':
					case 'E':  case 'F':
					{
						matchRange('A','F');
						break;
					}
					case '0':  case '1':  case '2':  case '3':
					case '4':  case '5':  case '6':  case '7':
					case '8':  case '9':
					{
						mDigit(false);
						break;
					}
					default:
					{
						if ( _cnt599>=1 ) { break _loop599; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					}
					_cnt599++;
				} while (true);
				}
				{
				_loop601:
				do {
					switch ( LA(1)) {
					case 'L':  case 'l':
					{
						mLongSuffix(false);
						break;
					}
					case 'U':  case 'u':
					{
						mUnsignedSuffix(false);
						break;
					}
					default:
					{
						break _loop601;
					}
					}
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					_ttype =  HEXADECIMALINT;
				}
			}
			else if ((LA(1)=='.') && (true)) {
				match('.');
				if ( inputState.guessing==0 ) {
					_ttype = DOT;
				}
				{
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					{
					int _cnt586=0;
					_loop586:
					do {
						if (((LA(1) >= '0' && LA(1) <= '9'))) {
							mDigit(false);
						}
						else {
							if ( _cnt586>=1 ) { break _loop586; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
						}
						
						_cnt586++;
					} while (true);
					}
					{
					if ((LA(1)=='E'||LA(1)=='e')) {
						mExponent(false);
					}
					else {
					}
					
					}
					if ( inputState.guessing==0 ) {
						_ttype = FLOATONE;
					}
					{
					switch ( LA(1)) {
					case 'F':  case 'f':
					{
						mFloatSuffix(false);
						break;
					}
					case 'L':  case 'l':
					{
						mLongSuffix(false);
						break;
					}
					default:
						{
						}
					}
					}
				}
				else {
				}
				
				}
			}
			else if ((LA(1)=='0') && (true) && (true)) {
				match('0');
				{
				_loop590:
				do {
					if (((LA(1) >= '0' && LA(1) <= '7'))) {
						matchRange('0','7');
					}
					else {
						break _loop590;
					}
					
				} while (true);
				}
				{
				_loop592:
				do {
					switch ( LA(1)) {
					case 'L':  case 'l':
					{
						mLongSuffix(false);
						break;
					}
					case 'U':  case 'u':
					{
						mUnsignedSuffix(false);
						break;
					}
					default:
					{
						break _loop592;
					}
					}
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					_ttype =  OCTALINT;
				}
			}
			else if (((LA(1) >= '1' && LA(1) <= '9')) && (true) && (true)) {
				matchRange('1','9');
				{
				_loop594:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDigit(false);
					}
					else {
						break _loop594;
					}
					
				} while (true);
				}
				{
				_loop596:
				do {
					switch ( LA(1)) {
					case 'L':  case 'l':
					{
						mLongSuffix(false);
						break;
					}
					case 'U':  case 'u':
					{
						mUnsignedSuffix(false);
						break;
					}
					default:
					{
						break _loop596;
					}
					}
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					_ttype =  DECIMALINT;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	public final void mID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ID;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop605:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			default:
			{
				break _loop605;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 288019269919178752L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-4398046520321L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=-9217L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { -9151595350857875456L, 95772161741946880L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 287949450930814976L, 541165879422L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[8];
		data[0]=-549755813889L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-17179878401L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 288019269919178752L, 137438953504L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	
	}
