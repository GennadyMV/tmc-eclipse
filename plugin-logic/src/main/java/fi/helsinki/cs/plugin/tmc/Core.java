package fi.helsinki.cs.plugin.tmc;

import fi.helsinki.cs.plugin.tmc.services.CourseFetcher;
import fi.helsinki.cs.plugin.tmc.services.Settings;

public class Core {
	
	private static Core core;
	
	private ErrorHandler errorHandler;
	private Settings settings;
	private CourseFetcher courseFetcher;

	private Core() {
		ServiceFactory factory = new ServiceFactory();
		this.settings = factory.getSettings();
		this.courseFetcher = factory.getCourseFetcher();
	}
	
	public static void setErrorHandler(ErrorHandler errorHandler) {
		Core.getInstance().errorHandler = errorHandler;
	}
	
	public static ErrorHandler getErrorHandler() {
		return Core.getInstance().errorHandler;
	}
	
	public static Settings getSettings(){
		return Core.getInstance().settings;
	}
	
	public static CourseFetcher getCourseFetcher(){
		return Core.getInstance().courseFetcher;
	}
	
	public static Core getInstance() {
		if(core == null) {
			core = new Core();
		}
		return core;
	}

}
