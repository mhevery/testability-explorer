package com.google.test.metric.cpp;

import java.util.BitSet;

/**
 * This class provides support for the grammar indexing of parsed tokens.
 */
class CPPvariables {
    /**
     * Marks an invalid construct.
     */
    public static final BitSet QI_INVALID = new BitSet(8);

    /**
     * Marks a type (includes enum, class, typedefs).
     */
    public static final BitSet QI_TYPE = new BitSet(8);

    /**
     * Marks a destructor.
     */
    public static final BitSet QI_DTOR = new BitSet(8);

    /**
     * Marks a constructor.
     */
    public static final BitSet QI_CTOR = new BitSet(8);

    /**
     * Marks an operator.
     */
    public static final BitSet QI_OPERATOR = new BitSet(8);

    /**
     * Marks a pointer to member.
     */
    public static final BitSet QI_PTR_MEMBER = new BitSet(8);

    /**
     * Marks a variable.
     */
    public static final BitSet QI_VAR = new BitSet(8);

    /**
     * Marks a function.
     */
    public static final BitSet QI_FUN = new BitSet(8);

    /**
     * Marks a ID. Not a type, but could be a var, func...
     */
    public static final BitSet QI_ID = new BitSet(8);

    /**
     * Initialization of the above markers.
     */
    static {
        QI_TYPE.set(0);
        QI_DTOR.set(1);
        QI_CTOR.set(2);
        QI_OPERATOR.set(3);
        QI_PTR_MEMBER.set(4);
        QI_ID.set(5);
        QI_VAR.set(6);
        QI_FUN.set(7);
    }

    /**
     * Maximum template token scan depth.
     */
    public static final int MAX_TEMPLATE_TOKEN_SCAN = 200;

    /**
     * Type def string identifier.
     */
    public static final String OT_TYPE_DEF = "otTypeDef";

    /**
     * Struct string identifier.
     */
    public static final String OT_STRUCT = "otStruct";

    /**
     * Union string identifier.
     */
    public static final String OT_UNION = "otUnion";

    /**
     * Enum string identifier.
     */
    public static final String OT_ENUM = "otEnum";

    /**
     * Class string identifier.
     */
    public static final String OT_CLASS = "otClass";

}
