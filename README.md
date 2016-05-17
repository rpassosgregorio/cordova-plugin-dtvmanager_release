# cordova-plugin-dtvchannelsearch
DTV Channel Search Plugin

Use only in STB with android box and SDMC

To call methods in javascript using callbacks:

	//Before call your functions, please verify device state
	//
	// Ex.:
	//
	// <body onload="init()">
	//	<script> 
	//	
	// function init(){
	//     document.addEventListener("deviceready", onDeviceReady, false);    
	// }
	//		
	// function onDeviceReady(){
	//		isDeviceReady = true;
	//	}
	//
	//

	//yourFunction	
	function yourFunction(){
		var callbackFunction = function(err, result){
			if(err){
				//your error code
			} else {
				//your error code				
			}
		}
		
		if(isDeviceReady){
			if(isDTVACPIManagerInit){
				//Calls one of the available methods
				DTVManager.stopScanDTVChannels(callbackFunction);
			} else {
				alert("Init DTVACPIManager before stop scan channels");
			}
		} else {
			alert("Waiting device become ready. try again");
		}
	}
	
	//To call one of the available methods	

		DTVManager.methodName(callbackFunction);		
	
	Available methods
	
		// Before call one of available methos, its required to initDTVACPIManager
	
		- initDTVACPIManager()
		- getAllPrograms()
		- scanDTVChannels()
		- stopScanDTVChannels()
		- deleteAllPrograms()
		- getNumberOfChannels()
		- showAllChannels()
		- getAllChannelsID()
		- getCurrProgram
		- playChannelByID() //Require to set: DTVManager.channelIDToPlay = ID_To_Play_Number
		- stopDTVACPIManager()
		- getEPGCurrNext()
		- getEPGByDayOfTheWeekIndex() //Require to set DTVManagerdayOfWeekIndex = dayOfTheWeekIndexNumber
		
		Method returns
		
		- initDTVACPIManager()
		
			/* Possibles values returns*/
			//**************************************************************************
			//*
			//* Errors - fires callback error
			//* 1 - Error initializing DTVACPIManager
			//*
			//* Success - Fires callback success
			//* 2 - Success initializing DTVACPIManager
			//**************************************************************************
		
		- getAllPrograms()
		
		- scanDTVChannels()
		
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - Already scanning
			//* 2 - Exception error
			//*
			//* Success -  - fires callback success
			//*
			//* 3 - Scan success
			//*
			//* For each scanned Channel returns in two steps:
			//*
			//* First success callback
			//*
			//* 	Returns:
			//*
			//* 	int Frequency, int Modulation, int BandWidth, int SymbolRare
			//* 	Ex: 761143,0,6,0
			//*
			//* Second success callback
			//*		Returns:
			//*
			//*		int channelID, string channelName, int channelNumber
			//*
			//***********************************************
		
		- stopScanDTVChannels()
		
		- deleteAllPrograms()
		
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - No channels in database
			//* 2 - Exception error
			//*
			//* Success -  - fires callback success
			//*
			//* 3 - All channels deleted
			//*
			//***********************************************
		
		- getNumberOfChannels()
			
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - Exception error
			//*
			//* Success -  - fires callback success
			//*
			//* 	Returns:
			//*
			//*		int iNumberOfChannels
			//*
			//*		Example:
			//*
			//*			if have 6 channels the return will be:
			//*			6
			//*
			//***********************************************
			
		- showAllChannels()
		
		- getAllChannelsID()
		
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - No channels in database
			//* 2 - Exception error
			//*
			//* Success -  - fires callback success
			//*
			//* 	Returns:
			//*
			//*		id01,id02,id03,...
			//*
			//*		Example:
			//*
			//*			if have 6 channels the result will be:
			//*			1,2,4,6,7,8
			//*
			//***********************************************
			
		- getCurrProgram
		
		- playChannelByID() //Require to set: DTVManager.channelIDToPlay = ID_To_Play_Number
		
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - No programs in database
			//* 2 - Already playing this program id
			//* 3 - no id in args - please pass an id
			//* 4 - Invalid ID
			//* 5 - Exception error
			//* Success -  - fires callback success
			//*
			//* 6 - OK - Playing passed channel by id
			//***********************************************
			
		- stopDTVACPIManager()
		
		- getEPGCurrNext()
		
			/* Possibles values returns*/
			//***********************************************
			//*
			//* Errors -  - fires callback error
			//*
			//* 1 - Exception erro
			//* 2 - No program running - need to play one channel to get epg
			//* 3 - No programs in database
			//*
			//* Success -  - fires callback success
			//*
			//* 	Returns:
			//*
			//*		Current_program_synopsis;Next_program_synopsis
			//*
			//*		Example:
			//*
			//*		this is the current synopsis; this is the next synopsis
			//*
			//***********************************************
		
		- getEPGByDayOfTheWeekIndex() //Require to set DTVManagerdayOfWeekIndex = dayOfTheWeekIndexNumber
	
	
	
	
	
	
	


	
