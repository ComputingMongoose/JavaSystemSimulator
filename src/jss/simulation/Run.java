package jss.simulation;

import java.io.IOException;

import org.json.JSONException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfigurationException;

public class Run {

	public static void main(String[] args) throws JSONException, IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		if(args.length!=1) {
			System.out.println("Run <simulation_folder>");
			return ;
		}
		
		SystemSettings settings=SystemSettings.getSystemSettings();
		System.out.println(String.format("Java System Simulator version %s",settings.getVersion()));
		System.out.println("https://github.com/ComputingMongoose/JavaSystemSimulator\n");
		System.out.println("Running simulation "+args[0]);
		Simulation sim=Simulation.loadFromFolder(args[0]);
		sim.start();
	}

}
