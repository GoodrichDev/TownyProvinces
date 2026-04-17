package io.github.townyadvanced.townyprovinces.jobs.land_validation;

import com.palmergames.bukkit.towny.scheduling.ScheduledTask;
import com.palmergames.bukkit.towny.object.Translatable;
import io.github.townyadvanced.townyprovinces.TownyProvinces;
import io.github.townyadvanced.townyprovinces.messaging.Messaging;

import java.util.Locale;

public class LandValidationTaskController {
	private static LandValidationJobStatus landValidationJobStatus;
	private static volatile int totalProvinces;
	private static volatile int processedProvinces;

	static {
		//Could actually be paused, so this could be misleading
		//Todo - Maybe improve in future. You would need to do a read in a separate thread to determine if paused
		landValidationJobStatus = LandValidationJobStatus.STOPPED;
	}

	private static ScheduledTask landValidationTask = null;
	public static void startTask() {
		totalProvinces = 0;
		processedProvinces = 0;
		landValidationTask = TownyProvinces.getPlugin().getScheduler().runAsync(new LandvalidationTask());
		landValidationJobStatus = LandValidationJobStatus.STARTED;
		Messaging.sendGlobalMessage(Translatable.of("msg_land_validation_job_started"));
	}
	
	public static void stopTask() {
		if(landValidationTask != null) {
			if (!landValidationTask.isCancelled() && !landValidationTask.isCurrentlyRunning()) {
				landValidationTask.cancel();
			}
			landValidationTask = null;
			landValidationJobStatus = LandValidationJobStatus.STOPPED;
			Messaging.sendGlobalMessage(Translatable.of("msg_land_validation_job_stopped"));
		}
	}

	public static void pauseTask() {
		if(landValidationTask != null) {
			if (!landValidationTask.isCancelled() && !landValidationTask.isCurrentlyRunning()) {
				landValidationTask.cancel();
			}
			landValidationTask = null;
			landValidationJobStatus = LandValidationJobStatus.PAUSED;
			Messaging.sendGlobalMessage(Translatable.of("msg_land_validation_job_paused"));
		}
	}

	public static void restartTask() {
		stopTask();
		startTask();
	}

	public static LandValidationJobStatus getLandValidationJobStatus() {
		return landValidationJobStatus;
	}

	public static void setLandValidationJobStatus(LandValidationJobStatus status) {
		landValidationJobStatus = status;
	}

	public static void setProgress(int processed, int total) {
		processedProvinces = processed;
		totalProvinces = total;
	}

	public static String getProgressSummary() {
		if (totalProvinces <= 0) {
			return "Initializing.";
		}

		double percent = (processedProvinces * 100.0) / totalProvinces;
		int remaining = Math.max(totalProvinces - processedProvinces, 0);
		return processedProvinces + "/" + totalProvinces + " (" + String.format(Locale.ROOT, "%.1f", percent) + "%), " + remaining + " remaining";
	}
	
}

 
