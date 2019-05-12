import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Add3;
import org.jgap.gp.function.Add4;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Multiply3;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

public class GPC extends GPProblem {
	  private ArrayList<Instance> instances;
	  private static ArrayList<Instance> trainInstances;
	  private static ArrayList<Instance> testInstances;
	  private static ArrayList<Variable> vars;
	  // max number of evolution.
	  private final static int maxEvo = 100;
	  // test data ratio
	  private double ratio = 0.3;
	  
	  public GPC() throws InvalidConfigurationException, IOException{
		  super(new GPConfiguration());
		  // Load data
		  loadData();
		  GPConfiguration config = getGPConfiguration();
		  vars = new ArrayList<>();
		  for(int i = 0; i < 9; i++) {
			  Variable var = Variable.create(config, "X" + i, CommandGene.IntegerClass);
			  vars.add(var);
		  }
		  config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
		  config.setMaxInitDepth(4);
		  config.setPopulationSize(1000);
	      config.setMaxCrossoverDepth(8);
		  config.setReproductionProb(0.05f);	      
	      config.setFitnessFunction(new SimpleFitnessFunction(trainInstances, vars));
	      config.setStrictProgramCreation(true);
	  }
	  
	  @Override
	  public GPGenotype create() throws InvalidConfigurationException {
		  GPConfiguration config = getGPConfiguration();
	      // The return type of the GP program.
	      Class[] types = { CommandGene.IntegerClass };
	      Class[][] argTypes = {{}};
	      CommandGene[][] nodeSets = {
	    		  {
	    			  vars.get(0),
	    			  vars.get(1),
	    			  vars.get(2),
	    			  vars.get(3),
	    			  vars.get(4),
	    			  vars.get(5),
	    			  vars.get(6),
	    			  vars.get(7),
	    			  vars.get(8),
	    			  new Add(config, CommandGene.IntegerClass),  
	    			  new Add3(config, CommandGene.IntegerClass),  
	    			  new Add4(config, CommandGene.IntegerClass),  
	    			  new Multiply(config, CommandGene.IntegerClass),
	    			  new Multiply3(config, CommandGene.IntegerClass),
	    			  new Divide(config, CommandGene.IntegerClass),
	    			  new Subtract(config, CommandGene.IntegerClass),
	    			  //new Pow(config, CommandGene.IntegerClass),
	    			  //new Exp(config, CommandGene.IntegerClass),
	    			  new Terminal(config, CommandGene.FloatClass, 0, 5.0, true)
	              }
	      };
	      GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets, 20, true);

	      return result;
	  }
	  
	  public static void test(final IGPProgram p) {
		    IGPProgram program = p;
			Object[] NO_ARGS = new Object[0];
			// training data classification
			int count = 0;
			for(int i = 0; i < trainInstances.size(); i++) {
				Instance inst = trainInstances.get(i);
				double ic = inst.getClassValue();
				ArrayList<Integer> attr = inst.getAttr();
				for(int j = 0; j < vars.size(); j++) {
					vars.get(j).set(attr.get(j));
				}
				double value = program.execute_int(0, NO_ARGS);
				if(value <= 0) {
					value = 2;
				} else {
					value = 4;
				}
				if(ic == (int) value) count++;
			}
			double accuracy = 0;
			if (count > 0) accuracy = ((double) count/ (double)trainInstances.size()) * 100;
			else accuracy = 0;
			System.out.println("Training Accuracy: " + count + " / " + trainInstances.size() + ", " + accuracy + "%");
			// test data classification
			program = p;
			count = 0;
			for(int i = 0; i < testInstances.size(); i++) {
				Instance inst = testInstances.get(i);
				float ic = inst.getClassValue();
				ArrayList<Integer> attr = inst.getAttr();
				for(int j = 0; j < vars.size(); j++) {
					vars.get(j).set(attr.get(j));
				}
				double value = program.execute_int(0, NO_ARGS);
				//System.out.println("Value: " + value);
				if(value <= 0) {
					value = 2;
				} else {
					value = 4;
				}
				if(ic == (int) value) count++;
			}
			if (count > 0) accuracy = ((double) count/ (double)testInstances.size()) * 100;
			else accuracy = 0;
			System.out.println("Test Accuracy: " + count + " / " + testInstances.size() + ", " + accuracy + "%");
	  }
	  
	  public void loadData() throws IOException {
		  String file = "breast-cancer-wisconsin.data";
		  instances = new ArrayList<>();
		  trainInstances = new ArrayList<>();
		  testInstances = new ArrayList<>();
		  
		  // Create training instance list
		  Scanner scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file)));
		  while(scan.hasNext()) {
			  String id = scan.next();
			  ArrayList<Integer> attr = new ArrayList<>();
			  for(int i = 0; i < 9; i++) {
				  String str = scan.next();
				  if(str.equals("?")) str = "-1";
				  int temp = Integer.parseInt(str);
				  attr.add(temp);
			  }
			  int ic = Integer.parseInt(scan.next());
			  Instance inst = new Instance(id, attr, ic);
			  instances.add(inst);
		  }
		  // Random 
		  int total = instances.size();
		  int trainSize = (int) (total * (1-ratio));
		  int count = 0;
		  int traincount = 0;
		  Random rand = new Random();
		  int r = rand.nextInt(total - trainSize);
		  for(Instance inst : instances) {
			  if(traincount < trainSize && count >= r) {
				  trainInstances.add(inst);
				  traincount++;
			  }
			  else testInstances.add(inst);
			  count++;
		  }
				  
		  /* Old way
		  for(Instance inst : instances) {
			  // training data should be the cancer
			  if(inst.getClassValue() == 4) trainInstances.add(inst);
			  // testing can be any of them but maybe it is godd to have whole
			  testInstances.add(inst);
		  }
		  */
		  BufferedOutputStream bs = null;
		  try {
			  // write training file
			  bs = new BufferedOutputStream(new FileOutputStream("training.txt"));
			  String str = "";
			  for(Instance inst : trainInstances) {
				  str += inst.toString();
			  }
			  bs.write(str.getBytes());
			  // write testing file
			  bs = new BufferedOutputStream(new FileOutputStream("test.txt"));
			  str = "";
			  for(Instance inst : testInstances) {
				  str += inst.toString();
			  }
			  bs.write(str.getBytes());
		  } catch (Exception e) {
			  e.getStackTrace();
		  }finally {
		      bs.close();
		  } 
	  }	  
	  
	  public static void main(String[] args) throws Exception {
		  GPProblem problem = new GPC();
	      GPGenotype gp = problem.create();
	      gp.setVerboseOutput(true);
	      gp.outputSolution(gp.getAllTimeBest());
	      double fitness = -1.0d;
		  for (int i = 1; i < maxEvo; i++) {
		    	System.out.println("Evolution: " + i);
	            gp.evolve();
	            gp.calcFitness();
	            fitness = gp.getAllTimeBest().getFitnessValue();
	            // if the fitness is small enough stop the evolution
	            if (fitness < 0.001d) break;
	       }
	      System.out.println("==========Finished============");
	      gp.outputSolution(gp.getAllTimeBest());
	      test(gp.getFittestProgramComputed());
	  }
}	  
