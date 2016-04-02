package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.AST.ArrayTypeNode;
import com.abcdabcd987.compiler2016.AST.FunctionCall;
import com.abcdabcd987.compiler2016.AST.StructTypeNode;
import com.abcdabcd987.compiler2016.AST.TypeNode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class GlobalSymbolTable {
    // Builtin type
    public final static PrimitiveType intType = new PrimitiveType(Type.Types.INT);
    public final static PrimitiveType boolType = new PrimitiveType(Type.Types.BOOL);
    public final static PrimitiveType voidType = new PrimitiveType(Type.Types.VOID);
    public final static PrimitiveType nullType = new PrimitiveType(Type.Types.NULL);
    public final static PrimitiveType stringType = new PrimitiveType(Type.Types.STRING);

    // Builtin string function
    private final static FunctionType stringLength = new FunctionType(intType, "string.length");
    private final static FunctionType stringSubString = new FunctionType(stringType, "string.substring") {{
        argTypes.add(intType);
        argTypes.add(intType);
    }};
    private final static FunctionType stringParseInt = new FunctionType(intType, "string.parseInt");
    private final static FunctionType stringOrd = new FunctionType(intType, "string.ord") {{
        argTypes.add(intType);
    }};
    public final static Map<String, FunctionType> stringBuiltinMethods = new HashMap<String, FunctionType>() {{
        put("length", stringLength);
        put("substring", stringSubString);
        put("parseInt", stringParseInt);
        put("ord", stringOrd);
    }};

    // Builtin function
    private final static FunctionType printFunc = new FunctionType(voidType, "print") {{
        argTypes.add(stringType);
    }};
    private final static FunctionType printlnFunc = new FunctionType(voidType, "println") {{
        argTypes.add(stringType);
    }};
    private final static FunctionType getStringFunc = new FunctionType(stringType, "getString");
    private final static FunctionType getIntFunc = new FunctionType(intType, "getInt");
    private final static FunctionType toStringFunc = new FunctionType(stringType, "toString") {{
        argTypes.add(intType);
    }};


    private Map<String, Type> typeMap = new LinkedHashMap<>();

    public SymbolTable globals = new SymbolTable(null);

    public GlobalSymbolTable() {
        typeMap.put("int", intType);
        typeMap.put("bool", boolType);
        typeMap.put("void", voidType);
        typeMap.put("null", nullType);
        typeMap.put("string", stringType);

        globals.define(printFunc.name, printFunc);
        globals.define(printlnFunc.name, printlnFunc);
        globals.define(getStringFunc.name, getStringFunc);
        globals.define(getIntFunc.name, getIntFunc);
        globals.define(toStringFunc.name, toStringFunc);
    }

    public void defineType(String name, Type type) {
        typeMap.put(name, type);
    }

    public Type resolveType(String name) {
        return typeMap.get(name);
    }

    public VariableType resolveVariableTypeFromAST(TypeNode node) {
        switch (node.getType()) {
            case INT: return intType;
            case BOOL: return boolType;
            case STRING: return stringType;
            case VOID: return voidType;
            case ARRAY:
                ArrayTypeNode t = (ArrayTypeNode) node;
                VariableType baseType = resolveVariableTypeFromAST(t.baseType);
                if (baseType != null) return new ArrayType(baseType);
                return null;
            case STRUCT:
                Type s = resolveType(((StructTypeNode) node).name);
                if (s != null) return (StructType) s;
                return null;
            default: return null;
        }
    }

    public String toStructureString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------GLOBAL TYPES:\n");
        typeMap.entrySet().stream().forEachOrdered(x ->
                sb.append(x.getKey()).append(": \n").append(x.getValue().toStructureString("  "))
        );
        sb.append("\n------GLOBAL SYMBOL TABLE:\n");
        sb.append(globals.toStructureString(""));
        return sb.toString();
    }
}