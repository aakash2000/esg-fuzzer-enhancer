package com.example.staticanalysis.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JimpleLocal;

import java.util.*;

public class ConstantPropagationAnalysis extends BodyTransformer {
    protected static Map<Value, Value[]> constantValues = new HashMap<>();

    protected static Map<String, Map<Value, Value[]>> methodConstantValues = new HashMap<>();
    protected static Map<String, Value> methodInvokeConstants = new HashMap<>();
    protected static Map<String, Value> methodReturnConstants = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ConstantPropagationAnalysis.class);

    List<String> CONSTANT_TO_CAPTURE = Arrays.asList(
            "soot.jimple.IntConstant",
            "soot.jimple.LongConstant",
            "soot.jimple.DoubleConstant",
            "soot.jimple.FloatConstant",
            "soot.jimple.StringConstant",
            "soot.jimple.NullConstant",
            "soot.jimple.ClassConstant",
            "soot.jimple.NumericConstant"
    );

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        logger.info("Analyzing method: " + body.getMethod().getName());

        // Clear the map for each new method body
        constantValues.clear();

        if (!body.getParameterLocals().isEmpty()) {
            for (Local local : body.getParameterLocals()) {
                if ((CONSTANT_TO_CAPTURE.contains(local.getClass().getName()) || local.getClass().getName().equals("soot.jimple.internal.JimpleLocal"))
                        && methodInvokeConstants.containsKey(body.getMethod().getName())) {
                    put(local, methodInvokeConstants.get(body.getMethod().getName()));
                }
            }
        }

        // Iterate through all units in the body
        for (Iterator<Unit> unitIt = body.getUnits().snapshotIterator(); unitIt.hasNext(); ) {
            Unit unit = unitIt.next();
            //System.out.println(unit);
            // Check if the unit is an assignment statement
            if (unit instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt) unit;
                handleAssignStmt(assignStmt);
            } else if (unit instanceof ReturnStmt) {
                ReturnStmt returnStmt = (ReturnStmt) unit;
                handleReturnStmt(body, returnStmt);
            } else if(unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
                handleStaticInvokeExpr(invokeExpr);
            }

            if (unit.branches()) {
                if (unit instanceof IfStmt) {
                    IfStmt ifStmt = (IfStmt) unit;
                    handleIfStmt(ifStmt);
                } else if (unit instanceof SwitchStmt) {
                    if (unit instanceof LookupSwitchStmt) {
                        LookupSwitchStmt switchStmt = (LookupSwitchStmt) unit;
                        handleLookupSwitchStmt(switchStmt);
                    } else if (unit instanceof TableSwitchStmt) {
                        TableSwitchStmt switchStmt = (TableSwitchStmt) unit;
                        //logger.info("TableSwitchStmt "+switchStmt);
                        // Analyze switch statement TODO
                    }
                }
            }
        }

        // Replace all local variables with their constant values
        for (Iterator<Unit> unitIt = body.getUnits().snapshotIterator(); unitIt.hasNext(); ) {
            Unit unit = unitIt.next();
            for (ValueBox valueBox : unit.getUseBoxes()) {
                Value value = valueBox.getValue();
                if (value instanceof Local) {
                    Local local = (Local) value;
                    if (constantValues.containsKey(local)) {
                        Value[] values = constantValues.get(local);
                        if (values.length > 0) {
                            logger.info("Replacing " + local + " with " + values[0]);
                            valueBox.setValue(values[0]);
                        }
                    }
                }
            }
        }

        // Store the constant values for this method
        if (!constantValues.isEmpty()) {
            // Create deep copy of constantValues map
            Map<Value, Value[]> constantValuesCopy = new HashMap<>();
            constantValues.forEach((k, v) -> {
                Value[] values = new Value[v.length];
                System.arraycopy(v, 0, values, 0, v.length);
                constantValuesCopy.put(k, values);
            });
            methodConstantValues.put(body.getMethod().getName(), constantValuesCopy);
        }
    }

    public Value getOp(Value op) {
        if (constantValues.containsKey(op)) {
            Value[] values = constantValues.get(op);
            if (values.length > 0) {
                return values[0];
            } else {
                return op;
            }
        } else {
            return op;
        }
    }

    public void handleAssignStmt(AssignStmt assignStmt) {
        Value rightOp = assignStmt.getRightOp();
        if (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName())) {
            Value leftOp = assignStmt.getLeftOp();
            if (leftOp instanceof StaticFieldRef || leftOp instanceof JimpleLocal) {
                //logger.info("Adding constant value: " + leftOp + " = " + rightOp);
                put(leftOp, rightOp);
            }
        } else if (rightOp instanceof AbstractBinopExpr) {
            //logger.info("Adding constant value: " + assignStmt.getLeftOp() + " = " + rightOp);
            Value value = evaluateExpr((AbstractBinopExpr) rightOp);
            if (value != null) {
                put(assignStmt.getLeftOp(), value);
            }
        } else if (rightOp instanceof UnopExpr) {
            //logger.info("Adding constant value: " + assignStmt.getLeftOp() + " = " + rightOp);
            Value value = evaluateExpr((UnopExpr) rightOp);
            if (value != null) {
                put(assignStmt.getLeftOp(), value);
            }
        } else if (rightOp instanceof InvokeExpr) {
            InvokeExpr invokeExpr = (InvokeExpr) rightOp;
            if (methodReturnConstants.containsKey(invokeExpr.getMethod().getName())) {
                Value value = methodReturnConstants.get(invokeExpr.getMethod().getName());
                put(assignStmt.getLeftOp(), value);
            }
        }
    }

    public void handleIfStmt(IfStmt ifStmt) {
        Value condition = ifStmt.getCondition();
        if (condition instanceof AbstractBinopExpr) {
            Value leftOp = ((AbstractBinopExpr) condition).getOp1();
            Value rightOp = ((AbstractBinopExpr) condition).getOp2();
            if (CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName())) {
                put(rightOp, leftOp);
            } else if (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName())) {
                put(leftOp, rightOp);
            }
        }
    }

    public void handleLookupSwitchStmt(LookupSwitchStmt switchStmt) {
        for (int i = 0; i < switchStmt.getLookupValues().size(); i++) {
            int lookupValue = switchStmt.getLookupValue(i);

            // Check if the value is within the ASCII range
            String compositeKey = switchStmt.getKey() + "_case" + i;
            if (lookupValue >= 0 && lookupValue <= 127) {
                char asciiChar = (char) lookupValue;

                // Store the character instead of the integer
                put(StringConstant.v(compositeKey), StringConstant.v(String.valueOf(asciiChar)));
            } else {
                // If it's not a valid ASCII value, handle as usual
                put(StringConstant.v(compositeKey), IntConstant.v(lookupValue));
            }
        }
    }
    public void put(Value key, Value value) {
        // Check if the key already exists in the map
        if (constantValues.containsKey(key)) {
            Value[] values = constantValues.get(key);

            // Check if the value already exists in the array for this key
            if (!Arrays.asList(values).contains(value)) {
                // If value does not exist, add it to the array
                Value[] newValues = new Value[values.length + 1];
                System.arraycopy(values, 0, newValues, 0, values.length);
                newValues[values.length] = value;
                constantValues.put(key, newValues);
            }
        } else {
            // If the key does not exist, create a new entry with this value
            constantValues.put(key, new Value[] { value });
        }
    }

    public Value evaluateExpr(AbstractBinopExpr expr) {
        if (expr instanceof AddExpr) {
            AddExpr addExpr = (AddExpr) expr;
            Value leftOp = addExpr.getOp1();
            Value rightOp = addExpr.getOp2();
            if ((CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName()) || constantValues.containsKey(leftOp)) &&
                    (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName()) || constantValues.containsKey(rightOp))) {
                Value leftValue = getOp(leftOp);
                Value rightValue = getOp(rightOp);
                if (leftValue instanceof IntConstant && rightValue instanceof IntConstant) {
                    IntConstant leftInt = (IntConstant) leftValue;
                    IntConstant rightInt = (IntConstant) rightValue;
                    return IntConstant.v(leftInt.value + rightInt.value);
                } else if (leftValue instanceof LongConstant && rightValue instanceof LongConstant) {
                    LongConstant leftLong = (LongConstant) leftValue;
                    LongConstant rightLong = (LongConstant) rightValue;
                    return LongConstant.v(leftLong.value + rightLong.value);
                } else if (leftValue instanceof DoubleConstant && rightValue instanceof DoubleConstant) {
                    DoubleConstant leftDouble = (DoubleConstant) leftValue;
                    DoubleConstant rightDouble = (DoubleConstant) rightValue;
                    return DoubleConstant.v(leftDouble.value + rightDouble.value);
                } else if (leftValue instanceof FloatConstant && rightValue instanceof FloatConstant) {
                    FloatConstant leftFloat = (FloatConstant) leftValue;
                    FloatConstant rightFloat = (FloatConstant) rightValue;
                    return FloatConstant.v(leftFloat.value + rightFloat.value);
                } else if (leftValue instanceof StringConstant && rightValue instanceof StringConstant) {
                    StringConstant leftString = (StringConstant) leftValue;
                    StringConstant rightString = (StringConstant) rightValue;
                    return StringConstant.v(leftString.value + rightString.value);
                }
            }
        } else if (expr instanceof SubExpr) {
            SubExpr subExpr = (SubExpr) expr;
            Value leftOp = subExpr.getOp1();
            Value rightOp = subExpr.getOp2();
            if ((CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName()) || constantValues.containsKey(leftOp)) &&
                    (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName()) || constantValues.containsKey(rightOp))) {
                Value leftValue = getOp(leftOp);
                Value rightValue = getOp(rightOp);
                if (leftValue instanceof IntConstant && rightValue instanceof IntConstant) {
                    IntConstant leftInt = (IntConstant) leftValue;
                    IntConstant rightInt = (IntConstant) rightValue;
                    return IntConstant.v(leftInt.value - rightInt.value);
                } else if (leftValue instanceof LongConstant && rightValue instanceof LongConstant) {
                    LongConstant leftLong = (LongConstant) leftValue;
                    LongConstant rightLong = (LongConstant) rightValue;
                    return LongConstant.v(leftLong.value - rightLong.value);
                } else if (leftValue instanceof DoubleConstant && rightValue instanceof DoubleConstant) {
                    DoubleConstant leftDouble = (DoubleConstant) leftValue;
                    DoubleConstant rightDouble = (DoubleConstant) rightValue;
                    return DoubleConstant.v(leftDouble.value - rightDouble.value);
                } else if (leftValue instanceof FloatConstant && rightValue instanceof FloatConstant) {
                    FloatConstant leftFloat = (FloatConstant) leftValue;
                    FloatConstant rightFloat = (FloatConstant) rightValue;
                    return FloatConstant.v(leftFloat.value - rightFloat.value);
                }
            }
        } else if (expr instanceof MulExpr) {
            MulExpr mulExpr = (MulExpr) expr;
            Value leftOp = mulExpr.getOp1();
            Value rightOp = mulExpr.getOp2();
            if ((CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName()) || constantValues.containsKey(leftOp)) &&
                    (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName()) || constantValues.containsKey(rightOp))) {
                Value leftValue = getOp(leftOp);
                Value rightValue = getOp(rightOp);
                if (leftValue instanceof IntConstant && rightValue instanceof IntConstant) {
                    IntConstant leftInt = (IntConstant) leftValue;
                    IntConstant rightInt = (IntConstant) rightValue;
                    return IntConstant.v(leftInt.value * rightInt.value);
                } else if (leftValue instanceof LongConstant && rightValue instanceof LongConstant) {
                    LongConstant leftLong = (LongConstant) leftValue;
                    LongConstant rightLong = (LongConstant) rightValue;
                    return LongConstant.v(leftLong.value * rightLong.value);
                } else if (leftValue instanceof DoubleConstant && rightValue instanceof DoubleConstant) {
                    DoubleConstant leftDouble = (DoubleConstant) leftValue;
                    DoubleConstant rightDouble = (DoubleConstant) rightValue;
                    return DoubleConstant.v(leftDouble.value * rightDouble.value);
                } else if (leftValue instanceof FloatConstant && rightValue instanceof FloatConstant) {
                    FloatConstant leftFloat = (FloatConstant) leftValue;
                    FloatConstant rightFloat = (FloatConstant) rightValue;
                    return FloatConstant.v(leftFloat.value * rightFloat.value);
                }
            }
        } else if (expr instanceof DivExpr) {
            DivExpr divExpr = (DivExpr) expr;
            Value leftOp = divExpr.getOp1();
            Value rightOp = divExpr.getOp2();
            if ((CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName()) || constantValues.containsKey(leftOp)) &&
                    (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName()) || constantValues.containsKey(rightOp))) {
                Value leftValue = getOp(leftOp);
                Value rightValue = getOp(rightOp);
                if (leftValue instanceof IntConstant && rightValue instanceof IntConstant) {
                    IntConstant leftInt = (IntConstant) leftValue;
                    IntConstant rightInt = (IntConstant) rightValue;
                    return IntConstant.v(leftInt.value / rightInt.value);
                } else if (leftValue instanceof LongConstant && rightValue instanceof LongConstant) {
                    LongConstant leftLong = (LongConstant) leftValue;
                    LongConstant rightLong = (LongConstant) rightValue;
                    return LongConstant.v(leftLong.value / rightLong.value);
                } else if (leftValue instanceof DoubleConstant && rightValue instanceof DoubleConstant) {
                    DoubleConstant leftDouble = (DoubleConstant) leftValue;
                    DoubleConstant rightDouble = (DoubleConstant) rightValue;
                    return DoubleConstant.v(leftDouble.value / rightDouble.value);
                } else if (leftValue instanceof FloatConstant && rightValue instanceof FloatConstant) {
                    FloatConstant leftFloat = (FloatConstant) leftValue;
                    FloatConstant rightFloat = (FloatConstant) rightValue;
                    return FloatConstant.v(leftFloat.value / rightFloat.value);
                }
            }
        } else if (expr instanceof RemExpr) {
            RemExpr remExpr = (RemExpr) expr;
            Value leftOp = remExpr.getOp1();
            Value rightOp = remExpr.getOp2();
            if ((CONSTANT_TO_CAPTURE.contains(leftOp.getClass().getName()) || constantValues.containsKey(leftOp)) &&
                    (CONSTANT_TO_CAPTURE.contains(rightOp.getClass().getName()) || constantValues.containsKey(rightOp))) {
                Value leftValue = getOp(leftOp);
                Value rightValue = getOp(rightOp);
                if (leftValue instanceof IntConstant && rightValue instanceof IntConstant) {
                    IntConstant leftInt = (IntConstant) leftValue;
                    IntConstant rightInt = (IntConstant) rightValue;
                    return IntConstant.v(leftInt.value % rightInt.value);
                } else if (leftValue instanceof LongConstant && rightValue instanceof LongConstant) {
                    LongConstant leftLong = (LongConstant) leftValue;
                    LongConstant rightLong = (LongConstant) rightValue;
                    return LongConstant.v(leftLong.value % rightLong.value);
                } else if (leftValue instanceof DoubleConstant && rightValue instanceof DoubleConstant) {
                    DoubleConstant leftDouble = (DoubleConstant) leftValue;
                    DoubleConstant rightDouble = (DoubleConstant) rightValue;
                    return DoubleConstant.v(leftDouble.value % rightDouble.value);
                } else if (leftValue instanceof FloatConstant && rightValue instanceof FloatConstant) {
                    FloatConstant leftFloat = (FloatConstant) leftValue;
                    FloatConstant rightFloat = (FloatConstant) rightValue;
                    return FloatConstant.v(leftFloat.value % rightFloat.value);
                }
            }
        }
        return null;
    }

    public Value evaluateExpr(UnopExpr expr) {
        if (expr instanceof NegExpr) {
            NegExpr negExpr = (NegExpr) expr;
            Value op = negExpr.getOp();
            if (CONSTANT_TO_CAPTURE.contains(op.getClass().getName()) || constantValues.containsKey(op)) {
                Value value = getOp(op);
                if (value instanceof IntConstant) {
                    IntConstant intConstant = (IntConstant) value;
                    return IntConstant.v(-intConstant.value);
                } else if (value instanceof LongConstant) {
                    LongConstant longConstant = (LongConstant) value;
                    return LongConstant.v(-longConstant.value);
                } else if (value instanceof DoubleConstant) {
                    DoubleConstant doubleConstant = (DoubleConstant) value;
                    return DoubleConstant.v(-doubleConstant.value);
                } else if (value instanceof FloatConstant) {
                    FloatConstant floatConstant = (FloatConstant) value;
                    return FloatConstant.v(-floatConstant.value);
                }
            }
        }
        return null;
    }

    public void handleReturnStmt(Body body, ReturnStmt returnStmt) {
        Value op = returnStmt.getOp();
        if (CONSTANT_TO_CAPTURE.contains(op.getClass().getName())) {
            methodReturnConstants.put(body.getMethod().getName(), op);
        } else if (constantValues.containsKey(op)) {
            Value[] values = constantValues.get(op);
            if (values.length > 0) {
                methodReturnConstants.put(body.getMethod().getName(), values[0]);
            }
        } else if (op instanceof AbstractBinopExpr) {
            Value value = evaluateExpr((AbstractBinopExpr) op);
            if (value != null) {
                methodReturnConstants.put(body.getMethod().getName(), value);
            }
        } else if (op instanceof UnopExpr) {
            Value value = evaluateExpr((UnopExpr) op);
            if (value != null) {
                methodReturnConstants.put(body.getMethod().getName(), value);
            }
        }
    }

    public void handleStaticInvokeExpr(InvokeExpr staticInvokeExpr) {
        if (!staticInvokeExpr.getArgs().isEmpty()) {
            for (Value arg : staticInvokeExpr.getArgs()) {
                if (CONSTANT_TO_CAPTURE.contains(arg.getClass().getName())) {
                    methodInvokeConstants.put(staticInvokeExpr.getMethod().getName(), arg);
                }
            }
        }
    }

    public Map<String, Map<Value, Value[]>> getMethodConstantValues() {
        return methodConstantValues;
    }
}
