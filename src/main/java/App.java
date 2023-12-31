import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.lang.reflect.Field;


public class App {
	
	private static Instance inst = Instance.inst1;
	private static long timeout = 60000; // five minutes
	private static boolean allSolutions;

	
	public static void main(String[] args) throws ParseException {

		final Options options = configParameters();
		final CommandLineParser parser = new DefaultParser();
		final CommandLine line = parser.parse(options, args);

		boolean helpMode = line.hasOption("help") || args.length == 0;
		if (helpMode) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cocoAirlines", options, true);
			System.exit(0);
		}
		// Check arguments and options
		for (Option opt : line.getOptions()) {
			checkOption(line, opt.getLongOpt());
		}
		
		System.out.println("\n------------------ GLIA Airlines ------------------ \n");
		
		GLIAAirlines avion = new GLIAAirlines();
		
		System.out.println("Question n°1 et 2 - Backtrack : \n");
		avion.divider(inst.nb_dividers, inst.capacity, inst.exits);
		System.out.println();
		
		System.out.println("\nQuestion n°5");
		avion.solve(inst, timeout, allSolutions);
		System.out.println();
	}
	

	
	// Add options here
	private static Options configParameters() {

		final Option helpFileOption = Option.builder("h").longOpt("help").desc("Display help message").build();

		final Option instOption = Option.builder("i").longOpt("instance").hasArg(true).argName("aircraft instance")
				.desc("aircraft instance (#dividers, capacity, exit doors) - from inst1 to inst10").required(false)
				.build();

		final Option allsolOption = Option.builder("a").longOpt("all").hasArg(false).desc("all solutions")
				.required(false).build();

		final Option limitOption = Option.builder("t").longOpt("timeout").hasArg(true).argName("timeout in ms")
				.desc("Set the timeout limit to the specified time").required(false).build();

		// Create the options list
		final Options options = new Options();
		options.addOption(instOption);
		options.addOption(allsolOption);
		options.addOption(limitOption);
		options.addOption(helpFileOption);

		return options;
	}
	

	// Check all parameters values
	public static void checkOption(CommandLine line, String option) {

		switch (option) {
		case "instance":
			inst = Instance.valueOf(line.getOptionValue(option));
			break;
		case "timeout":
			timeout = Long.parseLong(line.getOptionValue(option));
			break;
		case "all":
			allSolutions = true;
			break;
		default: {
			System.err.println("Bad parameter: " + option);
			System.exit(2);
		}

		}

	}
	

	
}
