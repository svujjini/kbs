/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kbs;

import java.io.*;
import java.util.*;


/**
 *
 * @author Jagan Vujjini
 */
public class KBS {
    public static ArrayList<String[]> attributes = new ArrayList<>();
    public static String[] attrs;
    public static HashMap<String, HashSet<String>> attributeMap = new HashMap<>();
    public static HashMap<String, HashSet<Integer>> varMap = new HashMap<>();
    public static String decision;
    public static Scanner sc=new Scanner(System.in);
    public static ArrayList<String> stable = new ArrayList<>();
    public static ArrayList<String> orderPairs = new ArrayList<>();
    public static String decisionPair;
    public static int minSupport;
    public static double minConfidence;
    static ArrayList<String> finalactionSet = new ArrayList<>();
    static TreeMap<String, Integer> actionSets = new TreeMap<>();
    public static TreeMap<String, Integer> firstLevel = new TreeMap<>();
    public static TreeMap<String, Integer> secondLevel = new TreeMap<>();
    public static TreeMap<String, Integer> thirdLevel = new TreeMap<>();
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        /*read input file*/
        BufferedReader br;
        String line;
        try {
	    br = new BufferedReader(new FileReader("C:\\Users\\Jagan Vujjini\\Desktop\\KBS\\src\\kbs\\input.csv"));
	    while ((line = br.readLine()) != null) {
		    attributes.add(line.split(","));
		}
			br.close();
		}  catch (IOException e) {
			System.out.println("Problem while reading the file!");
			System.exit(0);
		}
        /* Store in map*/
		System.out.println("Input");
		for (String[] strings : attributes) {
		    System.out.println(Arrays.asList(strings));
		}
		attrs = attributes.get(0);
		attributes.remove(0);
		for (int i = 0; i < attrs.length; i++) {
			HashSet<String> eachCol = new HashSet<>();
			for (String[] strings : attributes) {
				eachCol.add(strings[i]);
			}
			attributeMap.put(attrs[i], eachCol);
		}
		HashSet<String> allAttrs = new HashSet<>();
		for (HashSet<String> set : attributeMap.values()) {
			for (String string : set) {
				allAttrs.add(string);
			}
		}
		for (String attribute : allAttrs) {
			HashSet<Integer> varIndex = new HashSet<>();
			for (int i = 0; i < attributes.size(); i++) {
				for (String string : attributes.get(i)) {
					if (string.equalsIgnoreCase(attribute)) {
						varIndex.add(i + 1);
					}
				}
			}
			varMap.put(attribute, varIndex);
		}
                /*Read inputs*/
		System.out.println("Enter your decision attribute here:"+ Arrays.asList(attrs));
		decision = sc.next();
                /*stable attributes*/
		for (String string : attrs) {
			if (!string.equalsIgnoreCase(decision))
				stableAttribute(string);
		}
                /*flexible attributes*/
			for (String string : attrs) {
				ArrayList<String> values = new ArrayList<>();
				String flexible1;
				String flexible2;
				for (String variable : attributeMap.get(string)) {
					orderPairs.add("(" + string + "," + variable + ")");
					if (!stable.contains(string)) {
						values.add(variable);
					}
					if (values.size() == 2) {
						flexible1 = values.get(0) + "->" + values.get(1);
						orderPairs.add("(" + string + "," + flexible1 + ")");
						flexible2 = values.get(1) + "->" + values.get(0);
						orderPairs.add("(" + string + "," + flexible2 + ")");
						values.clear();
					}
				}
		}
		System.out.println(Arrays.asList(orderPairs));
                /*tansition*/
		System.out.println(" transition attribute from:");
		String attr1 = sc.next();
		System.out.println(" transition attribute to:");
		String attr2 = sc.next();
		if (attr1.equalsIgnoreCase(attr2)) {
			System.out.println("Both the transition values are same");
			System.exit(0);
		}
		decisionPair = attr1 + "->" + attr2;
		System.out.println("Enter support  from 1-" +attributes.size());
		minSupport = sc.nextInt();
		System.out.println("Enter confidence from 0-1");
		minConfidence = sc.nextDouble();
    /*generating action sets*/
    System.out.println("Action Sets:");
    new FirtIteration().calculateSingleOrderSupport();
    new SecondIteration().calculateSecondOrderSupport();
    new ThirdIteration().calculateThirdOrderSupport();
   
	}
    private static void stableAttribute(String string) {
		System.out.println("Is the attribute '" + string + "' stable(Y/N)");
		String value = sc.next();
		if (value.equalsIgnoreCase("Y")) {
			stable.add(string);
		}
	}
    public static class FirtIteration{
	private static void calculateSingleOrderSupport() {
		for (String string : orderPairs) {
			String var = string.split(",")[1].replace(")", "");
			int sup;
			if (var.contains("->")) {
				String[] variable = var.split("->");
				ArrayList<Integer> support = new ArrayList<>();
				for (String string2 : variable) {
					support.add(varMap.get(string2).size());
				}
				sup = Collections.min(support);
				} else {
				       sup = varMap.get(var).size();
				}
				firstLevel.put(string, sup);
			}
			System.out.println("First Level Action Sets");
			for (String strings : firstLevel.keySet()) {
				if(firstLevel.get(strings)>=minSupport)
				System.out.println(strings + "\tSupport: "+ firstLevel.get(strings));
			}
		}
	}
    public static class SecondIteration {
	private static void calculateSecondOrderSupport() {
		for (String r : firstLevel.keySet()) {
			for (String col : firstLevel.keySet()) {
				int sup = 0;
				String rVar = r.split(",")[0].replace("(", "");
				String cVar = col.split(",")[0].replace("(","");
				String rAttr = r.split(",")[1].replace(")", "");
				String cAttr = col.split(",")[1].replace(")","");
				if (!rAttr.contains("->") && !cAttr.contains("->")) {
					if (!rVar.equalsIgnoreCase(cVar)) {
						HashSet<Integer> rVal = varMap.get(rAttr);
						HashSet<Integer> cVal = varMap.get(cAttr);
						int count = 0;
						for (Integer rInt : rVal) {
							for (Integer cInt : cVal) {
								if (rInt == cInt)
									count++;
								}
							}
						sup = count;
						if (!secondLevel.containsKey(col + "."+ r)) {
							secondLevel.put(r + "." + col,sup);
						}
					}
				} else if (rAttr.contains("->") && !cAttr.contains("->")) {
					HashSet<Integer> rVal1 = varMap.get(rAttr.split("->")[0]);
					HashSet<Integer> rVal2 = varMap.get(rAttr.split("->")[1]);
					HashSet<Integer> cVal = varMap.get(cAttr);
					ArrayList<Integer> support = new ArrayList<Integer>();
					if (!rVar.equalsIgnoreCase(cVar)) {
						int count = 0;
						for (Integer cInt : cVal) {
							for (Integer rInt : rVal1) {
								if (rInt == cInt)
									count++;
							}
						}
						support.add(count);
						count = 0;
						for (Integer cInt : cVal) {
						for (Integer rInt : rVal2) {
						if (rInt == cInt)
								count++;
						}
					}
						support.add(count);
						sup = Collections.min(support);
						if (!secondLevel.containsKey(col + "."+ r)) {
							secondLevel.put(r + "." + col,sup);
						}
					}
				} else if (cAttr.contains("->") && !rAttr.contains("->")) {
					HashSet<Integer> cVal1 = varMap.get(cAttr.split("->")[0]);
					HashSet<Integer> cVal2 = varMap.get(cAttr.split("->")[1]);
					HashSet<Integer> rVal = varMap.get(rAttr);
                                        ArrayList<Integer> support = new ArrayList<Integer>();
						if (!rVar.equalsIgnoreCase(cVar)) {
							int count = 0;
							for (Integer cInt : rVal) {
								for (Integer rInt : cVal1) {
									if (rInt == cInt)
										count++;
								}
							}
							support.add(count);
							count = 0;
							for (Integer cInt : rVal) {
								for (Integer rInt : cVal2) {
									if (rInt == cInt)
										count++;
								}
							}
							support.add(count);
							sup = Collections.min(support);
							if (!secondLevel.containsKey(col + "."+ r)) {
								secondLevel.put(r + "." + col,sup);
							}
						}
					} else if (cAttr.contains("->") && rAttr.contains("->")) {
						HashSet<Integer> rVal1 = varMap.get(rAttr.split("->")[0]);
						HashSet<Integer> rVal2 = varMap.get(rAttr.split("->")[1]);
						HashSet<Integer> cVal1 = varMap.get(cAttr.split("->")[0]);
						HashSet<Integer> cVal2 = varMap.get(cAttr.split("->")[1]);

						ArrayList<Integer> support = new ArrayList<Integer>();
						if (!rVar.equalsIgnoreCase(cVar)) {
							int count = 0;
							for (Integer cInt : rVal1) {
								for (Integer rInt : cVal1) {
									if (rInt == cInt)
										count++;
								}
							}
							support.add(count);
							count = 0;
							for (Integer cInt : rVal2) {
								for (Integer rInt : cVal2) {
									if (rInt == cInt)
										count++;
								}
							}
							support.add(count);
							sup= Collections.min(support);
							if (!secondLevel.containsKey(col + "."+ r)) {
								secondLevel.put(r + "." + col,sup);
							}
						}
					}
				}
			}
			System.out.println("Second Order Action Sets");
			for (String strings : secondLevel.keySet()) {
				if (secondLevel.get(strings) >= minSupport){
					actionSets.put(strings, secondLevel.get(strings));
				System.out.println(strings + "\tSupport: "+ secondLevel.get(strings));
				}
			}
		}
		

	}
    public static class ThirdIteration{
	private static void calculateThirdOrderSupport() {
		TreeMap<String, Integer> temp = new TreeMap<String, Integer>();
		temp.putAll(secondLevel);
		for (String r : temp.keySet()) {
			if (secondLevel.get(r) >= minSupport) {
				for (String col : firstLevel.keySet()) {
					if (r.contains(".")) {
						int count = 0;
						for (String splittedValue : r.split("\\.")) {
							String rVar = splittedValue.split(",")[0].replace("(", "");
							String cVar = col.split(",")[0].replace("(", "");
							if (rVar.equalsIgnoreCase(cVar))
								count++;
						}
						if (count == 0) {
							String value1 = r.split("\\.")[0].split(",")[1].replace(")", "");
							String value2 = (r.split("\\.")[1]).split(",")[1].replace(")", "");
							String value3 = col.split(",")[1].replace(")","");
							int support = 0;
							if (!value1.contains("->") && !value2.contains("->") && !value3.contains("->")) {
								HashSet<Integer> values1 = varMap.get(value1);
								HashSet<Integer> values2 = varMap.get(value2);
								HashSet<Integer> values3 = varMap.get(value3);
								int flag = 0;
								for (Integer avalue1 : values1) {
									for (Integer bvalue1 : values2) {
										for (Integer cvalue1 : values3) {
											if (avalue1 == bvalue1 && avalue1 == cvalue1 && bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									support = flag;
									if (!thirdLevel.containsKey(col + "." + r)) {
										thirdLevel.put(r + "." + col,support);
									}
								} else if (!value1.contains("->") && !value2.contains("->") && value3.contains("->")) {
									HashSet<Integer> values1 = varMap.get(value1);
									HashSet<Integer> values2 = varMap.get(value2);
									String[] values3 = value3.split("->");
									HashSet<Integer> values31 = varMap.get(values3[0]);
									HashSet<Integer> values32 = varMap.get(values3[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1 && avalue1 == cvalue1 && bvalue1 == cvalue1)
														flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (!value1.contains("->")
										&& value2.contains("->")
										&& !value3.contains("->")) {
									HashSet<Integer> values1 = varMap
											.get(value1);
									HashSet<Integer> values2 = varMap
											.get(value3);
									String[] values3 = value2.split("->");
									HashSet<Integer> values31 = varMap
											.get(values3[0]);
									HashSet<Integer> values32 = varMap
											.get(values3[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (value1.contains("->")
										&& !value2.contains("->")
										&& !value3.contains("->")) {
									HashSet<Integer> values1 = varMap
											.get(value3);
									HashSet<Integer> values2 = varMap
											.get(value2);
									String[] values3 = value1.split("->");
									HashSet<Integer> values31 = varMap
											.get(values3[0]);
									HashSet<Integer> values32 = varMap
											.get(values3[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values1) {
										for (Integer bvalue1 : values2) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (value1.contains("->")
										&& value2.contains("->")
										&& !value3.contains("->")) {
									HashSet<Integer> values3 = varMap
											.get(value3);
									String[] values1 = value1.split("->");
									String[] values2 = value2.split("->");
									HashSet<Integer> values31 = varMap
											.get(values1[0]);
									HashSet<Integer> values32 = varMap
											.get(values1[1]);
									HashSet<Integer> values21 = varMap
											.get(values2[0]);
									HashSet<Integer> values22 = varMap
											.get(values2[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avauel1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values31) {
												if (avauel1 == bvalue1
														&& avauel1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avauel1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values32) {
												if (avauel1 == bvalue1
														&& avauel1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avauel1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values31) {
												if (avauel1 == bvalue1
														&& avauel1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);

									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (!value1.contains("->")
										&& value2.contains("->")
										&& value3.contains("->")) {
									HashSet<Integer> values3 = varMap
											.get(value1);
									String[] values1 = value3.split("->");
									String[] values2 = value2.split("->");
									HashSet<Integer> values31 = varMap
											.get(values1[0]);
									HashSet<Integer> values32 = varMap
											.get(values1[1]);
									HashSet<Integer> values21 = varMap
											.get(values2[0]);
									HashSet<Integer> values22 = varMap
											.get(values2[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);

									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (value1.contains("->")
										&& !value2.contains("->")
										&& value3.contains("->")) {
									HashSet<Integer> values3 = varMap
											.get(value2);
									String[] values1 = value1.split("->");
									String[] values2 = value3.split("->");
									HashSet<Integer> values31 = varMap
											.get(values1[0]);
									HashSet<Integer> values32 = varMap
											.get(values1[1]);
									HashSet<Integer> values21 = varMap
											.get(values2[0]);
									HashSet<Integer> values22 = varMap
											.get(values2[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values3) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);

									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								} else if (value1.contains("->")
										&& value2.contains("->")
										&& value3.contains("->")) {
									String[] values1 = value1.split("->");
									String[] values2 = value2.split("->");
									String[] values3 = value3.split("->");
									HashSet<Integer> values31 = varMap
											.get(values1[0]);
									HashSet<Integer> values32 = varMap
											.get(values1[1]);
									HashSet<Integer> values21 = varMap
											.get(values2[0]);
									HashSet<Integer> values22 = varMap
											.get(values2[1]);
									HashSet<Integer> values11 = varMap
											.get(values2[0]);
									HashSet<Integer> values12 = varMap
											.get(values3[1]);
									int flag = 0;
									ArrayList<Integer> supports = new ArrayList<Integer>();
									for (Integer avalue1 : values11) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values11) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values11) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values11) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values12) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values12) {
										for (Integer bvalue1 : values21) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values12) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values31) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									flag = 0;
									for (Integer avalue1 : values12) {
										for (Integer bvalue1 : values22) {
											for (Integer cvalue1 : values32) {
												if (avalue1 == bvalue1
														&& avalue1 == cvalue1
														&& bvalue1 == cvalue1)
													flag++;
											}
										}
									}
									supports.add(flag);
									support = Collections.min(supports);
									if (!thirdLevel.containsKey(col + "."
											+ r)) {
										thirdLevel.put(r + "." + col,
												support);
									}
								}

							}
						}
					}
				}
			}
			System.out.println("Third Order Action Sets");
			for (String strings : thirdLevel.keySet()) {
				if (thirdLevel.get(strings) >= minSupport){
					actionSets.put(strings, thirdLevel.get(strings));
				System.out.println("Action set: " + strings + "\tSupport: "
						+ thirdLevel.get(strings));
				}
			}
		}
    
    }
}


