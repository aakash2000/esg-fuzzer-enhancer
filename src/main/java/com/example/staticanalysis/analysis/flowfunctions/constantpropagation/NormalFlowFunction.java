package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.analysis.data.DFFManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Immediate;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.internal.*;

import java.util.*;

public class NormalFlowFunction extends GenericFlowFunction {

    private static final Logger logger = LoggerFactory.getLogger(NormalFlowFunction.class);

    private Unit curr;
    private Unit succ;

    public DFFManager dffManager = DFFManager.getInstance();

    public NormalFlowFunction(Unit curr, Unit succ) {
        this.curr = curr;
        this.succ = succ;
    }
    @Override
    public Set<DFF> computeTargets(DFF source) {
        logger.info("Normal computeTargets " + this.curr + " -> " + this.succ);
        Set<DFF> returnSet = new HashSet<>();
        if (this.curr != null) {
            /* Normal Flow Function */
            if (this.curr.getUseBoxes() != null) {
                this.curr.getUseBoxes().forEach(useBox -> {
                    if (useBox != null) {
                        if (useBox.getClass().getName() == "soot.jimple.internal.JAssignStmt$LinkedRValueBox") {
                            JAssignStmt.LinkedRValueBox linkedRValueBox = (JAssignStmt.LinkedRValueBox) useBox;
                            linkedRValueBox.getValue().getUseBoxes().forEach(useBox1 -> {
                                if (useBox1.getValue().getClass() == JimpleLocal.class) {
                                    DFFManager.getInstance().getDFFs().forEach(dff -> {
                                        if (dff.getValuePair() != null) {
                                            if (dff.getValuePair().getVariableName() == useBox1.getValue()) {
                                                useBox1.setValue(dff.getValuePair().getVariableValue());
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
            if (this.curr.getDefBoxes() != null) {
                this.curr.getDefBoxes().forEach(defBox -> {
                    Set<DFF> tempSet = new HashSet<>();
                    dffManager.getDFFs().forEach(dff -> {
                        if (dff.getMethod() != null && defBox.getClass().getEnclosingMethod() == dff.getMethod().getClass().getEnclosingMethod()) {
                            DFF newDFF = new DFF(defBox.getValue(), dff.getFact(), null);
                            tempSet.add(newDFF);
                            returnSet.add(newDFF);
                        }
                    });
                    if (!tempSet.isEmpty()) {
                        tempSet.forEach(dff -> {
                            dffManager.addDFF(dff);
                            returnSet.add(dff);
                        });
                    }
                    if (defBox.getValue().getClass() == JimpleLocal.class && this.curr.getUseBoxes() != null) {
                        this.curr.getUseBoxes().forEach(useBox -> {
                            if (useBox.getClass() == JAssignStmt.LinkedRValueBox.class) {
                                JAssignStmt.LinkedRValueBox linkedRValueBox = (JAssignStmt.LinkedRValueBox) useBox;
                                if (linkedRValueBox.getValue().getClass() == JAddExpr.class) {
                                    JAddExpr jAddExpr = (JAddExpr) linkedRValueBox.getValue();
                                    if (jAddExpr.getOp1() instanceof IntConstant && jAddExpr.getOp2() instanceof IntConstant) {
                                        IntConstant intConstant1 = (IntConstant) jAddExpr.getOp1();
                                        IntConstant intConstant2 = (IntConstant) jAddExpr.getOp2();
                                        int result = intConstant1.value + intConstant2.value;
                                        DFF newDFF = new DFF(defBox.getValue(), IntConstant.v(result), null);
                                        DFFManager.getInstance().addDFF(newDFF);
                                    }
                                } else if (linkedRValueBox.getValue().getClass() == JSubExpr.class) {
                                    JSubExpr jSubExpr = (JSubExpr) linkedRValueBox.getValue();
                                    if (jSubExpr.getOp1() instanceof IntConstant && jSubExpr.getOp2() instanceof IntConstant) {
                                        IntConstant intConstant1 = (IntConstant) jSubExpr.getOp1();
                                        IntConstant intConstant2 = (IntConstant) jSubExpr.getOp2();
                                        int result = intConstant1.value - intConstant2.value;
                                        DFF newDFF = new DFF(defBox.getValue(), IntConstant.v(result), null);
                                        DFFManager.getInstance().addDFF(newDFF);
                                    }
                                } else if (linkedRValueBox.getValue().getClass() == JMulExpr.class) {
                                    JMulExpr jMulExpr = (JMulExpr) linkedRValueBox.getValue();
                                    if (jMulExpr.getOp1() instanceof IntConstant && jMulExpr.getOp2() instanceof IntConstant) {
                                        IntConstant intConstant1 = (IntConstant) jMulExpr.getOp1();
                                        IntConstant intConstant2 = (IntConstant) jMulExpr.getOp2();
                                        int result = intConstant1.value * intConstant2.value;
                                        DFF newDFF = new DFF(defBox.getValue(), IntConstant.v(result), null);
                                        DFFManager.getInstance().addDFF(newDFF);
                                    }
                                } else if (linkedRValueBox.getValue().getClass() == JDivExpr.class) {
                                    JDivExpr jDivExpr = (JDivExpr) linkedRValueBox.getValue();
                                    if (jDivExpr.getOp1() instanceof IntConstant && jDivExpr.getOp2() instanceof IntConstant) {
                                        IntConstant intConstant1 = (IntConstant) jDivExpr.getOp1();
                                        IntConstant intConstant2 = (IntConstant) jDivExpr.getOp2();
                                        int result = intConstant1.value / intConstant2.value;
                                        DFF newDFF = new DFF(defBox.getValue(), IntConstant.v(result), null);
                                        DFFManager.getInstance().addDFF(newDFF);
                                    }
                                }
                            }
                        });
                    }

                });
            }

        }
        return Collections.singleton(source);
    }

}
