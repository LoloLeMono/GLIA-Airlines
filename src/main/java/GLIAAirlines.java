import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;
import static org.chocosolver.util.tools.ArrayUtils.append;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GLIAAirlines {

	
	IntVar[] dividers;
	Model model;

	public void solve(Instance inst, long timeout, boolean allSolutions) {
	    buildModel(inst);
	    
	    configureSearch();
	    
	    Solver solver = model.getSolver();
	    
	    solver.limitTime(timeout);
	    
	    if(allSolutions) {
	    	while(solver.solve()){
		    	System.out.print("Solution trouvée : ");
		        for (IntVar divider : dividers) 
		        	System.out.print(divider.getValue() + " ");
		        System.out.println();
	    	}
	    }else {
	    	Solution s = solver.findSolution();
		    if(s != null) {
		    	System.out.print("Solution trouvée : ");
		        for (IntVar divider : dividers) 
		        	System.out.print(divider.getValue() + " ");
		    }else {
		    	System.out.println("Pas de solution");
		    }
	    }

	   solver.printStatistics();
	}

	public void buildModel(Instance inst) {
	    // Créer une nouvelle instance de modèle
				
	    model = new Model("Aircraft Class Divider ");
	    int n = inst.nb_dividers;
	    int m = inst.capacity;
	    int[] exits = inst.exits;
	    
	    // Définition des variables
	    dividers = model.intVarArray("dividers", n, 0, m); // Positions des séparateurs
	    IntVar[] distances = model.intVarArray("distances", n * (n - 1) / 2, 1, m);

	    
	    // Contraintes
	    // 1. Séparateur à la position 0 et m
	    model.arithm(dividers[0], "=", 0).post(); 
	    model.arithm(dividers[n-1], "=", m).post(); 

    
	    for(int i=1; i<n-1; i++) {
		    // 2. Aucun séparateur à la position 1
	        model.arithm(dividers[i], "!=", 1).post();
		    // 3. Aucun séparateur aux positions des sorties de secours
	        for (int exit : exits) 
	            model.arithm(dividers[i], "!=", exit).post();
	    }

	    // 4. Principe de Golomb   
	    
	    
	    int k = 0;
	    for(int i=0; i<n-1; ++i) {
	    	model.arithm(dividers[i], "<", dividers[i+1]).post();
		    for(int j=i+1; j<n; ++j)
		    	model.scalar(new IntVar[]{dividers[i], dividers[j]}, new int[]{-1,1}, "=", distances[k++]).post();
		    	model.arithm(distances[0], "<", distances[distances.length-1]).post();
		    }
	    
	    model.allDifferent(distances).post();	   
	}



	public void configureSearch() {
		model.getSolver().setSearch(minDomLBSearch(append(dividers)));
	}
	
    public void divider(int n, int m, int[] exit) {
        int[] separators = new int[n];
        separators[0] = 0;  // Séparateur au début
        separators[n - 1] = m;  // Séparateur à la fin

        // On commence en position au moins en position 2 pour avoir les 2 blocs pour la classe à l'avant
        if (placeSeparators(1, 2, separators, exit, m)) {
            System.out.print("Solution trouvée : ");
            for (int i = 0; i < separators.length; i++) {
                System.out.print(separators[i] + " ");
            }
        } else {
            System.out.println("Pas de solution!");
        }
    }

    private boolean placeSeparators(int currentIndex, int position, int[] separators, int[] exit, int m) {
        if (currentIndex == separators.length - 1) {
            return true;
        }

        for (int i = position; i < m; i++) {
            if (isValid(i, separators, currentIndex, exit)) {
                separators[currentIndex] = i;
                if (placeSeparators(currentIndex + 1, i + 1, separators, exit, m)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValid(int position, int[] separators, int currentIndex, int[] exit) {
        // Un seprateur ne peut être dans le exit
        for (int e : exit) {
            if (e == position) return false;
        }
        
        int n = separators.length;

    	
        // Vérifier que la distance entre chaque paire de séparateurs est unique
        
        for(int i=0; i<currentIndex; i++) {
        	for(int j= i+1; j < currentIndex; j++) {
        		for(int k=0; k <= currentIndex; k++) {
                    if (separators[j] - separators[i] == separators[n-1] - position 
                    		|| separators[j] - separators[i] == position - separators[k] ) {
                        return false;
                    }
        		}
        	}
        }
        
        return true;
    }

}
