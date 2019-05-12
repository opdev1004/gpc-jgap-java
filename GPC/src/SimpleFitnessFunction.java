import java.util.ArrayList;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

public class SimpleFitnessFunction extends GPFitnessFunction {

	private ArrayList<Instance> trainInstances;
	  private ArrayList<Variable> vars;
	
	public SimpleFitnessFunction (ArrayList<Instance> trainInstances, ArrayList<Variable> vars) {
		this.trainInstances = trainInstances;
		this.vars = vars;
	}

	@Override
	protected double evaluate(final IGPProgram program) {
		double result = 0.0f;
		Object[] NO_ARGS = new Object[0];
		int count = 0;
		for(int i = 0; i < trainInstances.size(); i++) {
			Instance inst = trainInstances.get(i);
			ArrayList<Integer> attr = inst.getAttr();
			int ic = inst.getClassValue();
			for(int j = 0; j < vars.size(); j++) {
				vars.get(j).set(attr.get(j));
			}
			double value = program.execute_int(0, NO_ARGS);
			//System.out.println("Value: " + value + ", Class: " + ic);
			if (value < 0) {
				if (inst.getClassValue() == 4) count++;
			} else {
				if (inst.getClassValue() == 2) count++;
			}			
		}
		result = ((double) count / (double) trainInstances.size());
		//System.out.println("result: " + result);
		return result;
	}

	
}
