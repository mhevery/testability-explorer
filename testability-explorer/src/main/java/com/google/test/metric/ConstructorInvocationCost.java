package com.google.test.metric;

public class ConstructorInvocationCost extends MethodInvocationCost {

  public ConstructorInvocationCost(SourceLocation location,
      MethodCost methodCost, Reason costSourceType, Cost invocationCost) {
    super(location, methodCost, costSourceType, invocationCost);
  }

  @Override
  public void link(Cost directCost, Cost dependentCost,
      Cost constructorDependentCost) {
    constructorDependentCost.addWithoutLod(cost);
  }

}
