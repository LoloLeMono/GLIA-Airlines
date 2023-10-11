import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;
import static org.chocosolver.util.tools.ArrayUtils.append;

public class GLIAAirlines {

	
	IntVar[] dividers;

	Model model;

	

	public void solve(Instance inst, long timeout, boolean allSolutions) {

		buildModel(inst);
	    model.getSolver().limitTime(timeout);

	    // Résoudre le modèle
	    while(model.getSolver().solve()) {
	    	// Vérifier si une solution a été trouvée
		    if (model.getSolver().isFeasible() == ESat.TRUE) {
		        System.out.println("Solution trouvée:");
		        // Afficher les valeurs des variables (séparateurs)
		        for (IntVar divider : dividers) {
		            System.out.println(divider.getName() + " : " + divider.getValue());
		        }
		    } else {
		        System.out.println("Aucune solution trouvée.");
		    }
	    }

	    // Afficher les statistiques de résolution
	    model.getSolver().printStatistics();
	   
	}

	public void buildModel(Instance inst) {
		// A new model instance
		model = new Model("Aircraft Class Divider ");
		int n = inst.nb_dividers;
		int m = inst.capacity;
		int[] exits = inst.exits;
		
		// VARIABLES
		dividers = new IntVar[n];
		
		dividers = model.intVarArray("dividers", n, 2, m); // Positions des séparateurs
		
		// CONSTRAINTS
		// 1. Il doit exister un séparateur à la position 0 et un autre à la position m
		// model.arithm(dividers[0], "=", 0).post();
		// model.arithm(dividers[n - 1], "=", m).post();
				
		// 2. Aucun séparateur ne peut être placé à la position 1
		for (int i = 0; i < n; i++)
		{
			model.arithm(dividers[i], "!=", 1).post();
		}
		
		// 3. Aucun séparateur ne peut occuper la même position qu'une sortie de secours
        for (int i = 0; i < n; i++) {
            for (int exit : exits) {
                model.arithm(dividers[i], "!=", exit).post();
            }
        }
        
        /*
        // 4. Principe de Golomb
	    int maxDifference = m - 1; // La différence maximale possible entre les séparateurs
	
	    // Créez un tableau pour représenter la distance entre chaque paire de séparateurs
	    IntVar[] differences = new IntVar[n * (n - 1) / 2];
	    int idx = 0;
	
	    for (int i = 0; i < n - 1; i++) {
	        for (int j = i + 1; j < n; j++) {
	            differences[idx] = model.intVar("diff_" + i + "_" + j, 0, maxDifference);
	            model.distance(dividers[i], dividers[j], "=", differences[idx]).post();
	            idx++;
	        }
	    }
	
	    // Ajoutez la contrainte que toutes les différences doivent être distinctes
	    for (int i = 0; i < differences.length - 1; i++) {
	        for (int j = i + 1; j < differences.length; j++) {
	            model.arithm(differences[i], "!=", differences[j]).post();
	        }
	    }
	    */
	    
        
        // 5. Aucun séparateur ne peut occuper la même position qu'un autre séparateur
     	model.allDifferent(dividers).post();
	}


	public void configureSearch() {
		model.getSolver().setSearch(minDomLBSearch(append(dividers)));

	}


}
