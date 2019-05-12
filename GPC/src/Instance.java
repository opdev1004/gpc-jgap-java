import java.util.ArrayList;

public class Instance {
	/*
	    #  Attribute                     Domain
   -- -----------------------------------------
   1. Sample code number            id number
   2. Clump Thickness               1 - 10
   3. Uniformity of Cell Size       1 - 10
   4. Uniformity of Cell Shape      1 - 10
   5. Marginal Adhesion             1 - 10
   6. Single Epithelial Cell Size   1 - 10
   7. Bare Nuclei                   1 - 10
   8. Bland Chromatin               1 - 10
   9. Normal Nucleoli               1 - 10
  10. Mitoses                       1 - 10
  11. Class:                        (2 for benign, 4 for malignant)
  
  
  Clump Thickness (CT), Uniformity of Cell Size (USz), Uniformity of Cell Shape (UShp), Marginal Adhesion (MA),
  Single Epithelial Cell Size (SESz), Bare Nuclei (BN), Bland Chromatin (BC), Normal Nucleoli (NN), Mitoses (M).
  Sixteen of the instances also contain ¡°missing attributes¡± (denoted by ¡°?¡±).
  
	 */
	// attributes
	String id;
	ArrayList<Integer> attr;
	// class
	int ic;
	int estimated = 0;
	public Instance(String id, ArrayList<Integer> attr, int ic){
		this.id = id;
		this.attr = attr;
		this.ic = ic;
	}
	
	public void setEstimated(int estimated) {
		this.estimated = estimated;
	}
	
	public int getEstimated() {
		return estimated;
	}
	
	public String getID() {
		return id;
	}
	
	public int getClassValue(){
		return ic;
	}
	
	public ArrayList<Integer> getAttr(){
		return attr;
	}
	
	public String toString() {
		String str = id;
		for(int i = 0; i < attr.size(); i++) {
			str = str + " " + attr.get(i); 
		}
		str += " " + ic + "\n";
		return str;
	}
	
}
