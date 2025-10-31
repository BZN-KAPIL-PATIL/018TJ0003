package com.byzan.operations;

import java.io.File;

import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;

public class CreateFolder implements PathConstant {

	public static int FolderCreation()
	{
		File tempFiile = null;
		try
		{
			tempFiile = new File(INPUT);
			if(!tempFiile.exists())
			{
				tempFiile.mkdirs();
				MyLogger.info("Creating Input folder");

			}
			tempFiile = new File(OUTPUT);
			if(!tempFiile.exists())
			{
				tempFiile.mkdirs();
				MyLogger.info("Creating Output folder");

			}		
			tempFiile = new File(SUCCESS);
			if(!tempFiile.exists())
			{
				tempFiile.mkdirs();
				MyLogger.info("Creating Success folder");

			}
			tempFiile = new File(FAILURE);
			if(!tempFiile.exists())
			{
				tempFiile.mkdirs();
				MyLogger.info("Creating Failure folder");

			}
		}catch (Exception e) {

			MyLogger.error("Error in folder creation  "+e.toString());
			return 0;
		}


		return 1;

	}

}
