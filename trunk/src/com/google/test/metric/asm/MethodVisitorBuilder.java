/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric.asm;

import static com.google.test.metric.asm.SignatureParser.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.FieldInfo;
import com.google.test.metric.FieldNotFoundException;
import com.google.test.metric.JavaType;
import com.google.test.metric.LocalVariableInfo;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.ParameterInfo;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.BlockDecomposer;
import com.google.test.metric.method.Constant;
import com.google.test.metric.method.op.stack.ArrayLoad;
import com.google.test.metric.method.op.stack.ArrayStore;
import com.google.test.metric.method.op.stack.Convert;
import com.google.test.metric.method.op.stack.Duplicate;
import com.google.test.metric.method.op.stack.Duplicate2;
import com.google.test.metric.method.op.stack.GetField;
import com.google.test.metric.method.op.stack.Increment;
import com.google.test.metric.method.op.stack.Invoke;
import com.google.test.metric.method.op.stack.Load;
import com.google.test.metric.method.op.stack.MonitorEnter;
import com.google.test.metric.method.op.stack.MonitorExit;
import com.google.test.metric.method.op.stack.MultiANewArrayIns;
import com.google.test.metric.method.op.stack.Pop;
import com.google.test.metric.method.op.stack.RetSub;
import com.google.test.metric.method.op.stack.Return;
import com.google.test.metric.method.op.stack.Store;
import com.google.test.metric.method.op.stack.Swap;
import com.google.test.metric.method.op.stack.Throw;
import com.google.test.metric.method.op.stack.Transform;

public class MethodVisitorBuilder implements MethodVisitor {

  private final ClassInfo classInfo;
  private final String name;
  private final String desc;
  private final Visibility visibility;
  private final Map<Integer, Variable> slots = new HashMap<Integer, Variable>();
  private final BlockDecomposer block = new BlockDecomposer();
  private final List<Runnable> recorder = new ArrayList<Runnable>();
  private final ClassRepository repository;

  private final List<Integer> cyclomaticComplexity = new ArrayList<Integer>();
  private Variable methodThis;
  private int lineNumber;
  private final Map<Label, Integer> lineNumbers = new HashMap<Label, Integer>();
  private int startingLineNumber;
  private final List<ParameterInfo> parameters = new ArrayList<ParameterInfo>();
  private final List<LocalVariableInfo> localVariables = new ArrayList<LocalVariableInfo>();
  private final boolean isFinal;

  public MethodVisitorBuilder(ClassRepository repository, ClassInfo classInfo,
      String name, String desc, String signature, String[] exceptions,
      boolean isStatic, boolean isFinal, Visibility visibility) {
    this.repository = repository;
    this.classInfo = classInfo;
    this.name = name;
    this.desc = desc;
    this.isFinal = isFinal;
    this.visibility = visibility;
    int slot = 0;
    if (!isStatic) {
      Type thisType = JavaType.fromJava(classInfo.getName());
      methodThis = new LocalVariableInfo("this", thisType);
      slots.put(slot++, methodThis);
      localVariables.add((LocalVariableInfo) methodThis);
    }
    for (Type type : parse(desc).getParameters()) {
      ParameterInfo parameterInfo = new ParameterInfo("param_" + slot, type);
      parameters.add(parameterInfo);
      slots.put(slot++, parameterInfo);
      if (JavaType.isDoubleSlot(type)) {
        slot++;
      }
    }
  }

  public void visitJumpInsn(final int opcode, final Label label) {
    if (opcode == Opcodes.GOTO) {
      recorder.add(new Runnable() {
        public void run() {
          block.addOp(new Transform(lineNumber, "GOTO", null, null, null));
          block.unconditionalGoto(label);
        }
      });
    } else if (opcode == Opcodes.JSR) {
      recorder.add(new Runnable() {
        public void run() {
          block.jumpSubroutine(label, lineNumber);
        }
      });
    } else {
      recorder.add(new Runnable() {
        public void run() {
          cyclomaticComplexity.add(lineNumber);
          switch (opcode) {
            case Opcodes.IFEQ :
              if1("IFEQ");
              break;
            case Opcodes.IFNE :
              if1("IFNE");
              break;
            case Opcodes.IFLT :
              if1("IFLT");
              break;
            case Opcodes.IFGE :
              if1("IFGE");
              break;
            case Opcodes.IFGT :
              if1("IFGT");
              break;
            case Opcodes.IFLE :
              if1("IFLE");
              break;
            case Opcodes.IFNONNULL :
              if1("IFNONNULL");
              break;
            case Opcodes.IFNULL :
              if1("IFNULL");
              break;
            case Opcodes.IF_ACMPEQ :
              if2("IF_ACMPEQ");
              break;
            case Opcodes.IF_ACMPNE :
              if2("IF_ACMPNE");
              break;
            case Opcodes.IF_ICMPEQ :
              if2("IF_ICMPEQ");
              break;
            case Opcodes.IF_ICMPGE :
              if2("IF_ICMPGE");
              break;
            case Opcodes.IF_ICMPGT :
              if2("IF_ICMPGT");
              break;
            case Opcodes.IF_ICMPLE :
              if2("IF_ICMPLE");
              break;
            case Opcodes.IF_ICMPLT :
              if2("IF_ICMPLT");
              break;
            case Opcodes.IF_ICMPNE :
              if2("IF_ICMPNE");
              break;
            default :
              throw new UnsupportedOperationException("" + opcode);
          }
          block.conditionalGoto(label);
        }

        private void if1(String name) {
          block.addOp(new Transform(lineNumber, name, JavaType.INT, null, null));
        }

        private void if2(String name) {
          block.addOp(new Transform(lineNumber, name, JavaType.INT, JavaType.INT, null));
        }
      });
    }
  }

  public void visitTryCatchBlock(final Label start, final Label end,
      final Label handler, final String type) {
    recorder.add(new Runnable() {
      public void run() {
        if (type != null) {
          cyclomaticComplexity.add(getLineNumberForLable(handler));
        }
        block.tryCatchBlock(start, end, handler, type);
      }

    });
  }

  private Integer getLineNumberForLable(final Label label) {
    Integer line = lineNumbers.get(label);
    return line == null ? -1 : line;
  }

  public void visitTableSwitchInsn(int min, int max, final Label dflt,
      final Label[] labels) {
    recorder.add(new Runnable() {
      public void run() {
        for (Label label : labels) {
          if (label != dflt) {
            cyclomaticComplexity.add(getLineNumberForLable(label));
          }
        }
        block.addOp(new Pop(lineNumber, 1));
        block.tableSwitch(dflt, labels);
      }
    });
  }

  public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
      final Label[] labels) {
    recorder.add(new Runnable() {
      public void run() {
        for (Label label : labels) {
          if (label != null) {
            cyclomaticComplexity.add(getLineNumberForLable(label));
          }
        }
        block.addOp(new Pop(lineNumber, 1));
        block.tableSwitch(dflt, labels);
      }
    });
  }

  public void visitLocalVariable(String name, String desc, String signature,
      Label start, Label end, int slotNum) {
    Type type = JavaType.fromDesc(desc);
    Variable variable = slots.get(slotNum);
    if (variable == null) {
      LocalVariableInfo localVar = new LocalVariableInfo(name, type);
      slots.put(slotNum, localVar);
      localVariables.add(localVar);
    } else {
      variable.setName(name);
    }
  }

  public void visitLineNumber(final int line, final Label start) {
    lineNumbers.put(start, line);
    recorder.add(new Runnable() { // $6
      public void run() {
        if (lineNumber == 0) {
          startingLineNumber = line;
        }
        lineNumber = line;
      }
    });
  }

  public void visitEnd() {
    for (Runnable runnable : recorder) {
      runnable.run();
    }
    block.decomposeIntoBlocks();
    try {
      MethodInfo methodInfo = new MethodInfo(classInfo, name, startingLineNumber,
          desc, methodThis, parameters, localVariables, visibility,
          cyclomaticComplexity, block.getOperations(), isFinal);
      classInfo.addMethod(methodInfo);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Error in " + classInfo + "." + name
          + desc, e);
    }
  }

  public void visitTypeInsn(final int opcode, final String desc) {
    if (desc.length() == 1) {
      throw new IllegalStateException(
          "WARNING! I don't expect primitive types:" + desc);
    }
    final Type type = desc.contains(";") ? JavaType.fromDesc(desc) : JavaType
        .fromJava(desc);
    recorder.add(new Runnable() {
      public void run() {
        switch (opcode) {
          case Opcodes.NEW :
            Constant constant = new Constant("new", type);
            block.addOp(new Load(lineNumber, constant));
            break;
          case Opcodes.NEWARRAY :
          case Opcodes.ANEWARRAY :
            block.addOp(new Transform(lineNumber, "newarray", JavaType.INT, null,
                type.toArray()));
            break;
          case Opcodes.INSTANCEOF :
            block.addOp(new Transform(lineNumber, "instanceof", JavaType.OBJECT,
                null, JavaType.INT));
            break;
          case Opcodes.CHECKCAST :
            block
                .addOp(new Transform(lineNumber, "checkcast", type, null, type));
            break;
          default :
            throw new UnsupportedOperationException("" + opcode);
        }
      }
    });
  }

  public void visitVarInsn(final int opcode, final int var) {
    switch (opcode) {
      case Opcodes.ILOAD :
        load(var, JavaType.INT);
        break;
      case Opcodes.LLOAD :
        load(var, JavaType.LONG);
        break;
      case Opcodes.FLOAD :
        load(var, JavaType.FLOAT);
        break;
      case Opcodes.DLOAD :
        load(var, JavaType.DOUBLE);
        break;
      case Opcodes.ALOAD :
        load(var, JavaType.OBJECT);
        break;

      case Opcodes.ISTORE :
        store(var, JavaType.INT);
        break;
      case Opcodes.LSTORE :
        store(var, JavaType.LONG);
        break;
      case Opcodes.FSTORE :
        store(var, JavaType.FLOAT);
        break;
      case Opcodes.DSTORE :
        store(var, JavaType.DOUBLE);
        break;
      case Opcodes.ASTORE :
        store(var, JavaType.OBJECT);
        break;

      case Opcodes.RET :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new RetSub(lineNumber));
          }
        });
        break;
      default :
        throw new UnsupportedOperationException("opcode: " + opcode);
    }
  }

  private void store(final int var, final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Store(lineNumber, variable(var, type)));
      }
    });
  }

  private void load(final int var, final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Load(lineNumber, variable(var, type)));
      }
    });
  }

  private Variable variable(int varIndex, Type type) {
    Variable variable = slots.get(varIndex);
    if (variable == null) {
      LocalVariableInfo localVar = new LocalVariableInfo("local_" + varIndex, type);
      slots.put(varIndex, localVar);
      localVariables.add(localVar);
      variable = localVar;
    }
    Type varType = variable.getType();
    if (!varType.equals(type) && (type.isPrimitive() || varType.isPrimitive())) {
      // Apparently the compiler reuses local variables and it is possible
      // that the types change. So if types change we have to drop
      // the variable and try again.
      slots.put(varIndex, null);
      return variable(varIndex, type);
    }
    return variable;
  }

  public void visitLabel(final Label label) {
    recorder.add(new Runnable() { // $11
      public void run() {
        block.label(label);
      }
    });
  }

  public void visitLdcInsn(final Object cst) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Load(lineNumber, new Constant(cst, JavaType.fromClass(cst
            .getClass()))));
      }
    });
  }

  public void visitInsn(final int opcode) {
    switch (opcode) {
      case Opcodes.ACONST_NULL :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new Load(lineNumber, new Constant(null, JavaType.OBJECT)));
          }
        });
        break;
      case Opcodes.ICONST_M1 :
      case Opcodes.ICONST_0 :
      case Opcodes.ICONST_1 :
      case Opcodes.ICONST_2 :
      case Opcodes.ICONST_3 :
      case Opcodes.ICONST_4 :
      case Opcodes.ICONST_5 :
        loadConstant(opcode - Opcodes.ICONST_M1 - 1, JavaType.INT);
        break;
      case Opcodes.LCONST_0 :
      case Opcodes.LCONST_1 :
        loadConstant(opcode - Opcodes.LCONST_0, JavaType.LONG);
        break;
      case Opcodes.FCONST_0 :
      case Opcodes.FCONST_1 :
      case Opcodes.FCONST_2 :
        loadConstant(opcode - Opcodes.FCONST_0, JavaType.FLOAT);
        break;
      case Opcodes.DCONST_0 :
      case Opcodes.DCONST_1 :
        loadConstant(opcode - Opcodes.DCONST_0, JavaType.DOUBLE);
        break;
      case Opcodes.IALOAD :
        recordArrayLoad(JavaType.INT);
        break;
      case Opcodes.LALOAD :
        recordArrayLoad(JavaType.LONG);
        break;
      case Opcodes.FALOAD :
        recordArrayLoad(JavaType.FLOAT);
        break;
      case Opcodes.DALOAD :
        recordArrayLoad(JavaType.DOUBLE);
        break;
      case Opcodes.AALOAD :
        recordArrayLoad(JavaType.OBJECT);
        break;
      case Opcodes.BALOAD :
        recordArrayLoad(JavaType.BYTE);
        break;
      case Opcodes.CALOAD :
        recordArrayLoad(JavaType.CHAR);
        break;
      case Opcodes.SALOAD :
        recordArrayLoad(JavaType.SHORT);
        break;

      case Opcodes.IASTORE :
        recordArrayStore(JavaType.INT);
        break;
      case Opcodes.LASTORE :
        recordArrayStore(JavaType.LONG);
        break;
      case Opcodes.FASTORE :
        recordArrayStore(JavaType.FLOAT);
        break;
      case Opcodes.DASTORE :
        recordArrayStore(JavaType.DOUBLE);
        break;
      case Opcodes.AASTORE :
        recordArrayStore(JavaType.OBJECT);
        break;
      case Opcodes.BASTORE :
        recordArrayStore(JavaType.BYTE);
        break;
      case Opcodes.CASTORE :
        recordArrayStore(JavaType.CHAR);
        break;
      case Opcodes.SASTORE :
        recordArrayStore(JavaType.SHORT);
        break;
      case Opcodes.POP :
      case Opcodes.POP2 :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new Pop(lineNumber, opcode - Opcodes.POP + 1));
          }
        });
        break;
      case Opcodes.DUP :
      case Opcodes.DUP_X1 :
      case Opcodes.DUP_X2 :
        recorder.add(new Runnable() {
          public void run() {
            int offset = opcode - Opcodes.DUP;
            block.addOp(new Duplicate(lineNumber, offset));
          }
        });
        break;
      case Opcodes.DUP2 :
      case Opcodes.DUP2_X1 :
      case Opcodes.DUP2_X2 :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new Duplicate2(lineNumber, opcode - Opcodes.DUP2));
          }
        });
        break;
      case Opcodes.SWAP :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new Swap(lineNumber));
          }
        });
        break;
      case Opcodes.IRETURN :
        _return(JavaType.INT);
        break;
      case Opcodes.FRETURN :
        _return(JavaType.FLOAT);
        break;
      case Opcodes.ARETURN :
        _return(JavaType.OBJECT);
        break;
      case Opcodes.LRETURN :
        _return(JavaType.LONG);
        break;
      case Opcodes.DRETURN :
        _return(JavaType.DOUBLE);
        break;
      case Opcodes.ATHROW :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new Throw(lineNumber));
          }
        });
        break;
      case Opcodes.RETURN :
        _return(JavaType.VOID);
        break;
      case Opcodes.LCMP :
        operation("cmp", JavaType.LONG, JavaType.LONG, JavaType.INT);
        break;
      case Opcodes.FCMPL :
        operation("cmpl", JavaType.FLOAT, JavaType.FLOAT, JavaType.INT);
        break;
      case Opcodes.FCMPG :
        operation("cmpg", JavaType.FLOAT, JavaType.FLOAT, JavaType.INT);
        break;
      case Opcodes.DCMPL :
        operation("cmpl", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.INT);
        break;
      case Opcodes.DCMPG :
        operation("cmpg", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.INT);
        break;
      case Opcodes.LSHL :
        operation("shl", JavaType.LONG, JavaType.INT, JavaType.LONG);
        break;
      case Opcodes.LSHR :
        operation("shr", JavaType.LONG, JavaType.INT, JavaType.LONG);
        break;
      case Opcodes.LUSHR :
        operation("ushr", JavaType.LONG, JavaType.INT, JavaType.LONG);
        break;
      case Opcodes.LADD :
        operation("add", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LSUB :
        operation("sub", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LDIV :
        operation("div", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LREM :
        operation("rem", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LAND :
        operation("and", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LOR :
        operation("or", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LXOR :
        operation("xor", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.LMUL :
        operation("mul", JavaType.LONG, JavaType.LONG, JavaType.LONG);
        break;
      case Opcodes.FADD :
        operation("add", JavaType.FLOAT, JavaType.FLOAT, JavaType.FLOAT);
        break;
      case Opcodes.FSUB :
        operation("sub", JavaType.FLOAT, JavaType.FLOAT, JavaType.FLOAT);
        break;
      case Opcodes.FMUL :
        operation("mul", JavaType.FLOAT, JavaType.FLOAT, JavaType.FLOAT);
        break;
      case Opcodes.FREM :
        operation("rem", JavaType.FLOAT, JavaType.FLOAT, JavaType.FLOAT);
        break;
      case Opcodes.FDIV :
        operation("div", JavaType.FLOAT, JavaType.FLOAT, JavaType.FLOAT);
        break;
      case Opcodes.ISHL :
        operation("shl", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.ISHR :
        operation("shr", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IUSHR :
        operation("ushr", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IADD :
        operation("add", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.ISUB :
        operation("sub", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IMUL :
        operation("mul", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IDIV :
        operation("div", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IREM :
        operation("rem", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IAND :
        operation("and", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IOR :
        operation("or", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.IXOR :
        operation("xor", JavaType.INT, JavaType.INT, JavaType.INT);
        break;
      case Opcodes.DSUB :
        operation("sub", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.DOUBLE);
        break;
      case Opcodes.DADD :
        operation("add", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.DOUBLE);
        break;
      case Opcodes.DMUL :
        operation("mul", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.DOUBLE);
        break;
      case Opcodes.DDIV :
        operation("div", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.DOUBLE);
        break;
      case Opcodes.DREM :
        operation("rem", JavaType.DOUBLE, JavaType.DOUBLE, JavaType.DOUBLE);
        break;
      case Opcodes.L2I :
        convert(JavaType.LONG, JavaType.INT);
        break;
      case Opcodes.L2F :
        convert(JavaType.LONG, JavaType.FLOAT);
        break;
      case Opcodes.L2D :
        convert(JavaType.LONG, JavaType.DOUBLE);
        break;
      case Opcodes.LNEG :
        operation("neg", JavaType.LONG, null, JavaType.LONG);
        break;
      case Opcodes.F2I :
        convert(JavaType.FLOAT, JavaType.INT);
        break;
      case Opcodes.F2L :
        convert(JavaType.FLOAT, JavaType.LONG);
        break;
      case Opcodes.FNEG :
        operation("neg", JavaType.FLOAT, null, JavaType.FLOAT);
        break;
      case Opcodes.F2D :
        convert(JavaType.FLOAT, JavaType.DOUBLE);
        break;
      case Opcodes.D2I :
        convert(JavaType.DOUBLE, JavaType.INT);
        break;
      case Opcodes.D2L :
        convert(JavaType.DOUBLE, JavaType.LONG);
        break;
      case Opcodes.D2F :
        convert(JavaType.DOUBLE, JavaType.FLOAT);
        break;
      case Opcodes.DNEG :
        operation("neg", JavaType.DOUBLE, null, JavaType.DOUBLE);
        break;
      case Opcodes.I2L :
        convert(JavaType.INT, JavaType.LONG);
        break;
      case Opcodes.I2F :
        convert(JavaType.INT, JavaType.FLOAT);
        break;
      case Opcodes.I2D :
        convert(JavaType.INT, JavaType.DOUBLE);
        break;
      case Opcodes.I2B :
        convert(JavaType.INT, JavaType.BYTE);
        break;
      case Opcodes.I2C :
        convert(JavaType.INT, JavaType.CHAR);
        break;
      case Opcodes.I2S :
        convert(JavaType.INT, JavaType.SHORT);
        break;
      case Opcodes.INEG :
        operation("neg", JavaType.INT, null, JavaType.INT);
        break;
      case Opcodes.ARRAYLENGTH :
        operation("arraylength", JavaType.OBJECT.toArray(), null, JavaType.INT);
        break;
      case Opcodes.MONITORENTER :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new MonitorEnter(lineNumber));
          }
        });
        break;
      case Opcodes.MONITOREXIT :
        recorder.add(new Runnable() {
          public void run() {
            block.addOp(new MonitorExit(lineNumber));
          }
        });
        break;
      case Opcodes.NOP:
        recorder.add(new Runnable(){
          public void run() {
            block.addOp(new Transform(lineNumber, "NOP", null, null, null));
          }
        });
    }
  }

  private void operation(final String operation, final Type op1,
      final Type op2, final Type result) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Transform(lineNumber, operation, op1, op2, result));
      }
    });
  }

  private void convert(final Type from, final Type to) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Convert(lineNumber, from, to));
      }
    });
  }

  private void _return(final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Return(lineNumber, type));
      }
    });
  }

  private void recordArrayLoad(final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new ArrayLoad(lineNumber, type));
      }
    });
  }

  private void recordArrayStore(final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new ArrayStore(lineNumber, type));
      }
    });
  }

  private void loadConstant(final int constant, final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Load(lineNumber, new Constant(constant, type)));
      }
    });
  }

  public void visitFieldInsn(final int opcode, final String owner,
      final String name, final String desc) {
    switch (opcode) {
      case Opcodes.PUTSTATIC :
          recorder.add(new PutFieldRunnable(repository, owner, name, desc, true));
          break;
      case Opcodes.PUTFIELD :
        recorder.add(new PutFieldRunnable(repository, owner, name, desc, false));
        break;
      case Opcodes.GETSTATIC :
          recorder.add(new GetFieldRunnable(repository, owner, name, desc, true));
          break;
      case Opcodes.GETFIELD :
        recorder.add(new GetFieldRunnable(repository, owner, name, desc, false));
        break;
    }
  }

  public void visitMethodInsn(final int opcode, final String clazz,
      final String name, final String desc) {
    SignatureParser signature = parse(desc);
    final List<Type> params = signature.getParameters();
    final Type returnType = signature.getReturnType();
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Invoke(lineNumber, clazz.replace('/', '.'), name, desc,
            params, opcode == Opcodes.INVOKESTATIC, returnType));
      }
    });
  }

  public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
    return null;
  }

  public AnnotationVisitor visitAnnotationDefault() {
    return null;
  }

  public void visitAttribute(Attribute arg0) {
  }

  public void visitCode() {
  }

  public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3,
      Object[] arg4) {
  }

  public void visitIincInsn(final int var, final int increment) {
    recorder.add(new Runnable() {
      public void run() {
        Variable variable = variable(var, JavaType.INT);
        block.addOp(new Increment(lineNumber, increment, variable));
      }
    });
  }

  public void visitIntInsn(int opcode, int operand) {
    switch (opcode) {
      case Opcodes.NEWARRAY :
        newArray(operand, toType(operand));
        break;
      case Opcodes.BIPUSH :
        loadConstant(operand, JavaType.INT);
        break;
      case Opcodes.SIPUSH :
        loadConstant(operand, JavaType.INT);
        break;
      default :
        throw new UnsupportedOperationException("Unexpected opcode: " + opcode);
    }
  }

  private Type toType(int operand) {
    switch (operand) {
      case Opcodes.T_BOOLEAN :
        return JavaType.BOOLEAN;
      case Opcodes.T_BYTE :
        return JavaType.BYTE;
      case Opcodes.T_CHAR :
        return JavaType.CHAR;
      case Opcodes.T_DOUBLE :
        return JavaType.DOUBLE;
      case Opcodes.T_FLOAT :
        return JavaType.FLOAT;
      case Opcodes.T_INT :
        return JavaType.INT;
      case Opcodes.T_LONG :
        return JavaType.LONG;
      case Opcodes.T_SHORT :
        return JavaType.SHORT;
      default :
        throw new IllegalArgumentException();
    }
  }

  private void newArray(final int operand, final Type type) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new Transform(lineNumber, "newarray", JavaType.INT, null, type
            .toArray()));
      }
    });
  }

  public void visitMaxs(int maxStack, int maxLocals) {
  }

  public void visitMultiANewArrayInsn(final String clazz, final int dims) {
    recorder.add(new Runnable() {
      public void run() {
        block.addOp(new MultiANewArrayIns(lineNumber, JavaType.fromDesc(clazz),
            dims));
      }
    });
  }

  public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
      boolean arg2) {
    return null;
  }

  @Override
  public String toString() {
    return classInfo + "." + name + desc + "\n" + block;
  }

  private class PutFieldRunnable implements Runnable {
    private final String fieldOwner;
    private final String fieldName;
    private final String fieldDesc;
    private final boolean isStatic;
    private final ClassRepository repository;

    public PutFieldRunnable(ClassRepository repository, String owner, String name, String desc,
        boolean isStatic) {
      this.repository = repository;
      this.fieldOwner = owner;
      this.fieldName = name;
      this.fieldDesc = desc;
      this.isStatic = isStatic;
    }

    public void run() {
      FieldInfo field = null;
      ClassInfo ownerClass = repository.getClass(fieldOwner);
      try {
        field = ownerClass.getField(fieldName);
      } catch (FieldNotFoundException e) {
        field =
            new FieldInfo(ownerClass, "FAKE:" + fieldName, JavaType
                .fromDesc(fieldDesc), false, isStatic, false);
      }
      block.addOp(new com.google.test.metric.method.op.stack.PutField(
          lineNumber, field));
    }
  }

  private class GetFieldRunnable implements Runnable {
    private final String fieldOwner;
    private final String fieldName;
    private final String fieldDesc;
    private final boolean isStatic;
    private final ClassRepository repository;

    public GetFieldRunnable(ClassRepository repository, String owner, String name, String desc,
        boolean isStatic) {
      this.repository = repository;
      this.fieldOwner = owner;
      this.fieldName = name;
      this.fieldDesc = desc;
      this.isStatic = isStatic;
    }

    public void run() {
      FieldInfo field = null;
      ClassInfo ownerClass = repository.getClass(fieldOwner);
      try {
        field = ownerClass.getField(fieldName);
      } catch (FieldNotFoundException e) {
        field = new FieldInfo(ownerClass, "FAKE:" + fieldName, JavaType
                .fromDesc(fieldDesc), false, isStatic, false);
      }
      block.addOp(new GetField(lineNumber, field));
    }

  }
}
