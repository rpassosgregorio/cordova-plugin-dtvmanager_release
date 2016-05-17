/* Termo de licensa Apache Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package com.apptec.dtvmanager;

//Bibliotecas
/*Bibliotecas Android*/
import android.app.Activity;
import android.util.Log;
import android.view.Window;

/*Bibliotecas cordova*/
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

/*Bibliotecas Java*/

import java.util.List;
import java.lang.*;//Thread.*;

/*Bibliotecas SDMV - AIDL - low level*/
import com.sdmc.aidl.ProgramParcel;
import com.sdmc.aidl.SearchParcel;

/*### Bibliotecas SDMV - ACPI - high level ###*/
import com.sdmc.dtv.acpi.DTVACPIManager;
import com.sdmc.dtv.acpi.ProgramInfo;
import com.sdmc.dtv.acpi.ProgramSearch;
import com.sdmc.dtv.acpi.TunerInfo;
//import com.sdmc.dtv.acpi.QuickIntegration;

public class DTVManager extends CordovaPlugin {
    private static final String TAG = "DTVChannelSearch";
	//final Activity activityX = this.cordova.getActivity();
	
	/*Para instanciar, DTVACPIManager tem que ser inicializado*/
	private ProgramInfo piProgramInfo;
	private ProgramSearch psProgramSearch;
	private TunerInfo tiTunerInfo;
	private SearchParcel spSearchParcel;
	private SearchParcel spEndSearchParcel;
	private ProgramParcel ppInfoCurrentProgram;
	//private QuickIntegration mQuickIntegration;
	/*Requer level 3 para ser acessado*/
	//private DTVInfo mDTVInfo;
	
	/*Licença SDMC DTV*/
	private static final String LICENSE = "4BC3EE167DAE1AE42FA0C6712F73D3199ADE8C2A242A60FC1815E56E1C6E5B3D003F217FAE62473BBD21B671D42E2A0A447B1A5C48EC1AD72B807AC9E69F5385";
	
	/*Licensa não válida para teste*/
	//private static final String LICENSE = "license";
	
	/*DTVACPIManager não inicializado*/
	private boolean bInitSuccess = false;
	private int iLevel = 0;
	private boolean bStopSearch = false;
	private int iCurrFreqSearch = 0;
	private boolean bIsScanning = false;
	
	/*Para pegar excessões do método: */
	/*private DTVACPIManager.OnInitCompleteListener mOnInitFinishListener*/	
	/*Motivo: fora do método exec*/
	private String sTest = " - sTest Não alterado";

	/* Inicialização do plugin
	* Sets the context of the Command. This can then be used to do things like
	* get file paths associated with the Activity.
	*
	* @param cordova The context of the main Activity.
	* @param webView The CordovaWebView Cordova is running in.
	*/
    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        Log.v(TAG, "DTVChannelSearch: initialization");
        super.initialize(cordova, webView);
    }

    /* Executa a ação requerida e retorna Pluginresult     
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    @Override
    public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        //Log.v(TAG, "Executing action: " + action);
        final Activity activity = this.cordova.getActivity();
        final Window window = activity.getWindow();

		//Inicialização do DTVACPIManager
		//Escopo do método:
		//
		//public static boolean init(android.content.Context context,
		//						   java.lang.String license,
		//						   DTVACPIManager.OnInitCompleteListener listener)
		//
		//context -
		//license - string da licença
		//listener - listener completo da inicialização - callback???
		//
		//##############################################
		//Escopo do método DTVACPIManager.OnInitCompleteListener:
		//Obs.: Será chamado após completar a inicialização de DTVInterfaceManager
		//
		//void onInitComplete(boolean isInitSuccess, int level)

		//Inicia DTVACPIManager

		if("initDTVACPIManager".equals(action)) {
			PluginResult prResult = new PluginResult(PluginResult.Status.NO_RESULT);

			/* Possibles values returns*/
			//**************************************************************************
			//*
			//* Errors - fires callback error
			//* 1 - Error initializing DTVACPIManager
			//*
			//* Success - Fires callback success
			//* 2 - Success initializing DTVACPIManager
			//**************************************************************************

			try {

				DTVACPIManager.init(activity, LICENSE, mOnInitFinishListener);
				//Falta colocar timeout
				while (bInitSuccess == false) {
					Thread.yield();
				}

				prResult = new PluginResult(PluginResult.Status.OK,2);
				prResult.setKeepCallback(true);
				callbackContext.sendPluginResult(prResult);
				return true;
			} catch (Exception e){
				prResult = new PluginResult(PluginResult.Status.ERROR,1);
				prResult.setKeepCallback(true);
				callbackContext.sendPluginResult(prResult);
				return true;
			}
		}

		//Ação do plugin - parâmetro "action"
		if("getNumberOfChannels".equals(action)) {

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

			PluginResult prResultNbr = new PluginResult(PluginResult.Status.NO_RESULT);

			try{
				piProgramInfo = new ProgramInfo();
				//List<ProgramParcel> ppAllChannels;
				//ppAllChannels = piProgramInfo.getPrograms();

				prResultNbr = new PluginResult(PluginResult.Status.OK, piProgramInfo.getPrograms().size());
				prResultNbr.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultNbr);
				return true;

			} catch (Exception e){
				prResultNbr = new PluginResult(PluginResult.Status.OK,1);
				prResultNbr.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultNbr);
				return true;
			}
		}

		//Ação do plugin - parâmetro "action"
		if("showAllChannels".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					PluginResult prResultSAC = new PluginResult(PluginResult.Status.NO_RESULT);

					try {
						piProgramInfo = new ProgramInfo();
                        //List<ProgramParcel> ppShowAllChannels;
						//ppShowAllChannels = piProgramInfo.getPrograms();

                        if(piProgramInfo.getPrograms().size() == 0){
                            prResultSAC = new PluginResult(PluginResult.Status.OK,"No channels");
                            prResultSAC.setKeepCallback(true);
                            callbackContext.sendPluginResult(prResultSAC);
                            return;
                        }

						int iNbrChannelsX = piProgramInfo.getPrograms().size();
                        for (int y = 0; iNbrChannelsX > y; y++) {
                            prResultSAC = new PluginResult(PluginResult.Status.OK,
                                    "Channels ID: " + piProgramInfo.getPrograms().get(y).getId() +
                                            " - Channels Number: " + piProgramInfo.getPrograms().get(y).getProgramNumber() +
                                            " - Channels Name: " + piProgramInfo.getPrograms().get(y).getName());
                            prResultSAC.setKeepCallback(true);
                            callbackContext.sendPluginResult(prResultSAC);
                        }
                        /*
						for (int y = 0; iNbrChannelsX > y; y++) {
							prResultSAC = new PluginResult(PluginResult.Status.OK,
											"Channels ID: " + ppShowAllChannels.get(y).getId() +
											" - Channels Number: " + ppShowAllChannels.get(y).getProgramNumber() +
											" - Channels Name: " + ppShowAllChannels.get(y).getName());
							prResultSAC.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultSAC);
						}*/

						//List<ProgramParcel> ppAllChannels;
						//ppAllChannels = piProgramInfo.getPrograms();
					} catch (Exception e) {
						prResultSAC = new PluginResult(PluginResult.Status.OK, "ERROR IN JAVA (showAllChannels) ");
						prResultSAC.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultSAC);
					}
				}
			});
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("getAllChannelsID".equals(action)) {

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

			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					PluginResult prResultGACID = new PluginResult(PluginResult.Status.NO_RESULT);
					String buffer = "";

					try {
						piProgramInfo = new ProgramInfo();

						if(piProgramInfo.getPrograms().size() == 0){
							prResultGACID = new PluginResult(PluginResult.Status.ERROR,1);
							prResultGACID.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGACID);
							return;
						}

						int iNbrChannelsX = piProgramInfo.getPrograms().size();
						for (int y = 0; iNbrChannelsX > y; y++) {
							buffer +=  piProgramInfo.getPrograms().get(y).getId();
							if(y < iNbrChannelsX-1) {
								buffer += ",";
							}
						}
						prResultGACID = new PluginResult(PluginResult.Status.OK,buffer);
						prResultGACID.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGACID);
					} catch (Exception e) {
						prResultGACID = new PluginResult(PluginResult.Status.OK, 2);
						prResultGACID.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGACID);
					}
				}
			});
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("deleteAllPrograms".equals(action)) {

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

			cordova.getThreadPool().execute( new Runnable()	{
				public void run() {

						boolean bIsDeleted;
					String bufferX;

					PluginResult prResultDel;

					try	{
						piProgramInfo = new ProgramInfo();
						List<ProgramParcel> ppAllChannelsX;
						int iNbrChannels = 0;
						int iInitialNbrOfChannels;
						int iCurrProgramID;


						try{
							iCurrProgramID = piProgramInfo.getCurrentProgram().getId();
							piProgramInfo.lockProgram(iCurrProgramID);

							prResultDel = new PluginResult(PluginResult.Status.OK,
									"<br>CurrProgram:  " + iCurrProgramID);
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						} catch (Exception e){
							prResultDel = new PluginResult(PluginResult.Status.OK,
									"<br>No current program running");
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}

						ppAllChannelsX = piProgramInfo.getPrograms();
						iNbrChannels = ppAllChannelsX.size();
						iInitialNbrOfChannels = iNbrChannels;
						if (iNbrChannels > 0) {

							/*prResultDel = new PluginResult(PluginResult.Status.OK,
									"Deleting " + iInitialNbrOfChannels + " programs<br>");
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);*/

                            int iID;

							for (int x = 0, j = 0; iInitialNbrOfChannels > x; ) {

                                iID = ppAllChannelsX.get(iNbrChannels-1).getId();
								bIsDeleted = piProgramInfo.deleteProgram(iID);
                                bufferX = "Deleted " + (j+1) + " programs --> ID: " + iID +
                                        //" --> Channel Name: " +ppAllChannels.get(x).getName() +
                                        " --> Nbr of Channels: " + (iNbrChannels - 1) +
                                        " --> piProgramInfo size: " + piProgramInfo.getPrograms().size() +
                                        " --> X = " + x +
                                        " - deleteProgramResult: ";
                                prResultDel = new PluginResult(PluginResult.Status.OK, bufferX + bIsDeleted);
                                prResultDel.setKeepCallback(true);
                                callbackContext.sendPluginResult(prResultDel);
                                iNbrChannels--;
                                x++;
                                //j++;
							}
							prResultDel = new PluginResult(PluginResult.Status.OK, 3);
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						} else {
							prResultDel = new PluginResult(PluginResult.Status.ERROR, 1);
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}
					} catch (Exception e) {
						prResultDel = new PluginResult(PluginResult.Status.ERROR, 2);
						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);
					}
				}
			});
			return true;
		}

		if("stopScanDTVChannels".equals(action)) {
			bStopSearch = true;
			iCurrFreqSearch = 0;
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("scanDTVChannels".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {

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

					PluginResult prResultRun;

					if(bIsScanning){
						prResultRun = new PluginResult(PluginResult.Status.ERROR,1);
						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);
					}

					iCurrFreqSearch = 0;

					//Not used
					//searchListener slSearchListener = new searchListener();

					//Callback mProgramSearch.setSearchListener(pslListener);
					ProgramSearch.ProgramSearchListener pslListener
							= new ProgramSearch.ProgramSearchListener() {

						PluginResult prResultListenerPS;
						//@Override
						public void onBeginOneFreq(final SearchParcel parcel) {
									try{

									} catch (Exception e){
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,"OnBeginOneFreq - Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}
									/*try {
										prResultListenerPS = new PluginResult(PluginResult.Status.OK,
														"<br>" + "Parcel Frequency: " + parcel.getFrequency() + " - " +
														"Parcel Modulation: " + parcel.getModulation() + " - " +
														"Parcel BandWidth: " + parcel.getBandWidth() + " - " +
														"Parcel SymbolRate: " + parcel.getSymbolRate() + " - " +
														"Parcel Describe Contents: " + parcel.describeContents()
										);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									} catch (Exception e) {
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"<br>Retorno callback ERROR (1) ProgramSearchListener onBeginOneFreq --> " +
														"Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}*/
						}
						//@Override
						public void onEndOneFreq(final int currentFreqIndex, final int FreqCount, final ProgramParcel[] parcels) {
									try {
										iCurrFreqSearch = currentFreqIndex;
										/*prResultListenerPS = new PluginResult(PluginResult.Status.OK,
												"<br>" + "Current Freq Index: " + currentFreqIndex + " - " +
														"CCurrent Freq Index Rodrigo: " + iCurrFreqSearch +
													"<br>" + parcels[0].getId() + "," + parcels[0].getName() +
													"," + parcels[0].getProgramNumber()
										);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);*/
									} catch (Exception e){
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,"onEndOneFreq - Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}
									/*try {
										if(parcels.length > 0) {
											if(parcels.length == 1){
												iCurrFreqSearch++;
												prResultListenerPS = new PluginResult(PluginResult.Status.OK,
														"<br>" + "Current Freq Index: " + currentFreqIndex + " - " +
																"CCurrent Freq Index Rodrigo: " + iCurrFreqSearch +
																"<br>" + parcels[0].getId() + "," + parcels[0].getName() +
																"," + parcels[0].getProgramNumber()
												);
												prResultListenerPS.setKeepCallback(true);
												callbackContext.sendPluginResult(prResultListenerPS);
											}
											if(parcels.length>1) {
												for (int i = (parcels.length-1); i >= 0; i--) {
													if (i == 0) {
														iCurrFreqSearch++;
													}
													prResultListenerPS = new PluginResult(PluginResult.Status.OK,
															"<br>" + "Loop - Current Freq Index: " + currentFreqIndex + " - " +
																	"Current Freq Index Rodrigo: " + iCurrFreqSearch +
																	"parcel lenght: " + parcels.length + " - i: " + i
																	"<br>" + parcels[i].getId() + "," + parcels[currentFreqIndex].getName() +
																	"," + parcels[i].getProgramNumber()
													);
													prResultListenerPS.setKeepCallback(true);
													callbackContext.sendPluginResult(prResultListenerPS);
												}
											}
										} else {
											iCurrFreqSearch++;
											prResultListenerPS = new PluginResult(PluginResult.Status.OK,
													"<br>" + "No parcel - Current Freq Index: " + currentFreqIndex + " - " +
															"Current Freq Index Rodrigo: " + iCurrFreqSearch
											);
											prResultListenerPS.setKeepCallback(true);
											callbackContext.sendPluginResult(prResultListenerPS);
										}

									} catch (Exception e) {
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"Retorno callback ERROR (2) ProgramSearchListener onEndOneFreq --> " +
														"Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}*/
						}

					};
					//End Callback private ProgramSearch.ProgramSearchListener pslListener = new ProgramSearch.ProgramSearchListener()

					try {

						int iStartFrequency = 177143;
						int iEndFrequency = 803143;
						int iEndIndex = 63;
						int iBandWidth = 6000;
						bStopSearch = false;
						boolean bStatusAntena;

						tiTunerInfo = new TunerInfo();
						piProgramInfo = new ProgramInfo();
						List<ProgramParcel> ppAllChannels;
						List<SearchParcel> getSearchParcelLists;
						spSearchParcel = new SearchParcel(iStartFrequency, iBandWidth);
						spEndSearchParcel = new SearchParcel(iEndFrequency, iBandWidth);
						psProgramSearch = new ProgramSearch();
						//ppInfoCurrentProgram = piProgramInfo.getCurrentProgram();

						bStatusAntena = tiTunerInfo.getAntennaPowerOnOff();
						if (!bStatusAntena) {
							tiTunerInfo.setAntennaPowerOnOff(true);
							bStatusAntena = tiTunerInfo.getAntennaPowerOnOff();
						}

						psProgramSearch.setSearchListener(pslListener);
						//psProgramSearch.autoSearchByIndex(iStartIndex,iEndIndex);
						psProgramSearch.autoSearch(spSearchParcel, spEndSearchParcel);
						bIsScanning = true;

						while (iCurrFreqSearch < iEndIndex) {
							if(bStopSearch){
								psProgramSearch.stopAutoSearch();
								break;
							}
							Thread.yield();
						}

						if(!bStopSearch) {
							psProgramSearch.stopAutoSearch();
						}

						bStopSearch = false;
						bIsScanning = false;
						iCurrFreqSearch = 0;
						tiTunerInfo.setAntennaPowerOnOff(false);

						prResultRun = new PluginResult(PluginResult.Status.OK, 3);
						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);

					} catch (Exception e) {
						//Callback Erro
						prResultRun = new PluginResult(PluginResult.Status.ERROR, 2);
						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);
						//return;

					}

					//Métodos testados funcionando - usados para testes

					//Muda o canal
					//mProgramInfo.playProgram(3);

					//Get Search frequency list
					//List<SearchParcel> lFreqList = mProgramSearch.getSearchParcelList();

					//getStreamTime - verificar o que é
					//String sStreamTime = mProgramInfo.getStreamTime();

					//Inicia o módulo para buscar canais digitais pela antena
					//mQuickIntegration.startSearchActivity();

					//Pega programa corrente e próximo do EPG
					//List<String> lsPFEPG = mProgramInfo.getPFEPG();

					//Pega programação de hoje e dos próximos 6 dias
					//List<String> lsTodayEPG = mProgramInfo.getEPG(0);
					//List<String> lsTomorrowEPG = mProgramInfo.getEPG(1);

					//Pega informações do programa corrente
					//ProgramParcel mInfoCurrentProgram = mProgramInfo.getCurrentProgram();

					//Status do sinal do programa corrente
					//int[] iaSignalStatus = mTunerInfo.getSignalStatus();

					//Precisa testar essa forma de pegar o nome do canal
					//String sName = ProgramParcel.getName();

					//Fim métodos testados

					//Métodos retornando erro
					//Requer level 3 - não acessível
					//boolean bIsAPPInit = mDTVInfo.isAPPInit();
					//String sGetTunerType = mDTVInfo.getTunerType();

				}//End public void Run() - getThreadPool				

			});	//End getThreadPool
			return true;
		}// End if("getBandWidth".equals(action))

		//Ação do plugin - parâmetro "action"
		if("getCurrProgram".equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultDel = new PluginResult(PluginResult.Status.NO_RESULT);
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultDel = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");

							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}

						prResultDel = new PluginResult(PluginResult.Status.OK,
														"<br>Current Playing Channel Info:" +
														"<br><br>ID: " + piProgramInfo.getCurrentProgram().getId() +
														"<br>Channel Number: " + piProgramInfo.getCurrentProgram().getProgramNumber() +
														"<br>Channel Name: " +piProgramInfo.getCurrentProgram().getName()
														);

						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);

					} catch (Exception e){

						prResultDel = new PluginResult(PluginResult.Status.OK,
								"<br>No current program running");
						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;
		}// End if("getCurrProgram".equals(action))

		//Ação do plugin - parâmetro "action"
		if("getEPGByDayOfTheWeekIndex".equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultGetEPGByDay;
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");

							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						if(args.isNull(0)){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
									"<br>Need to pass an day index to get the EPG (0 to 6)<br>Current Day = 0, Tomorrow = 1, ..., Last EPG Day = 6");
							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
										"<br>EPG of day with index: " + args.getInt(0) +
										"<br><br>EPG: " + piProgramInfo.getEPG(args.getInt(0))
						);

						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);

					} catch (Exception e){

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
								"<br>No current program running. Need to play program to get EPG");
						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;

		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if ("getEPGCurrNext".equals(action)) {
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
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultGetEPGByDay;
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, 3);

							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						try{
							piProgramInfo.getCurrentProgram().getId();
						} catch (Exception e){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, 2);
							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
						}

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, piProgramInfo.getPFEPG().toString()
						);

						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);

					} catch (Exception e){

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, 1);
						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;

		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if ("getSearchedFreqList".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {

					PluginResult prResultGSFL = new PluginResult(PluginResult.Status.NO_RESULT);
					try {
						int iNumberOfSearchedFrequencies;
						psProgramSearch = new ProgramSearch();
						List<SearchParcel> getSearchParcelLists;
						getSearchParcelLists = psProgramSearch.getSearchParcelList();
						iNumberOfSearchedFrequencies = getSearchParcelLists.size();

						prResultGSFL = new PluginResult(PluginResult.Status.OK,
								"<br>Number of Searched Frequencies: " + getSearchParcelLists.size() + "<br>");
						prResultGSFL.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGSFL);

						if (iNumberOfSearchedFrequencies == 0) {
							return;
						}

						for (int i = 0; iNumberOfSearchedFrequencies > i; i++) {
							prResultGSFL = new PluginResult(PluginResult.Status.OK,
									"<br>Frequency " + i + " - " + getSearchParcelLists.get(i).getFrequency());
							prResultGSFL.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGSFL);
						}


					} catch (Exception e){
						prResultGSFL = new PluginResult(PluginResult.Status.OK,
								"<br>ERROR GETTING SEARCHED FREQUENCY LIST");
						prResultGSFL.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGSFL);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;
		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if("playChannelByID".equals(action)) {

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

			PluginResult prResultPCId;
			boolean b = true;
			try {
				//String sChannelID = args.get(0).toString();
				piProgramInfo = new ProgramInfo();
				int iNumberOfChannels = piProgramInfo.getPrograms().size();

				if (iNumberOfChannels == 0) {
					prResultPCId = new PluginResult(PluginResult.Status.ERROR, 1);
					prResultPCId.setKeepCallback(true);
					callbackContext.sendPluginResult(prResultPCId);
					return true;
				}

				if (args.isNull(0)) {
					prResultPCId = new PluginResult(PluginResult.Status.ERROR, 3);
					prResultPCId.setKeepCallback(true);
					callbackContext.sendPluginResult(prResultPCId);
					return true;
				}

				int iChannelID = Integer.parseInt(args.get(0).toString());
				int iCurrPlayingChannel = piProgramInfo.getCurrentProgram().getId();

				if (iChannelID == iCurrPlayingChannel) {
					prResultPCId = new PluginResult(PluginResult.Status.ERROR, 2);

					prResultPCId.setKeepCallback(true);
					callbackContext.sendPluginResult(prResultPCId);
					return true;
				}


				for (int i = 0; i < iNumberOfChannels; i++) {
					if (iChannelID == piProgramInfo.getPrograms().get(i).getId()) {
						piProgramInfo.playProgram(iChannelID);
						prResultPCId = new PluginResult(PluginResult.Status.OK, 6);
						prResultPCId.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultPCId);
						return true;
					}
				}

				prResultPCId = new PluginResult(PluginResult.Status.ERROR, 4);
				prResultPCId.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultPCId);
				return true;

			} catch (Exception e) {
				prResultPCId = new PluginResult(PluginResult.Status.ERROR, 5);
				prResultPCId.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultPCId);
				return true;
			}
		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if("stopDTVACPIManager".equals(action)) {

			PluginResult prResult;
			try {
				if (!bInitSuccess) {
					prResult = new PluginResult(PluginResult.Status.OK,
							"<br><b>DTVACPIManager não inicializado. Não há necessidade de pará-lo</b>" +
									"<br>InitSuccess = " + bInitSuccess +
									"<br>Access Level: " + iLevel);
					prResult.setKeepCallback(true);
					callbackContext.sendPluginResult(prResult);
					return true;
				}

				DTVACPIManager.release();

				bInitSuccess = false;
				iLevel = 0;

				prResult = new PluginResult(PluginResult.Status.OK,
						"<br><b>DTVACPIManager Stopped</b>" +
								"<br>InitSuccess = " + bInitSuccess +
								"<br>Access Level: " + iLevel);
				prResult.setKeepCallback(true);
				callbackContext.sendPluginResult(prResult);
				return true;
			} catch (Exception e){
				prResult = new PluginResult(PluginResult.Status.OK,
						"<br><b>DTVACPIManager Stopped</b>" +
								"<br>InitSuccess = " + bInitSuccess +
								"<br>Access Level: " + iLevel);
				prResult.setKeepCallback(true);
				callbackContext.sendPluginResult(prResult);
				return true;
			}
		}// End if("getAllPrograms".equals(action))

		//If action not equals any planned actions, return false - Invalid action
        return false;
    }// End public boolean execute
	
	//Callback inicialização DTVACPIManager
	private DTVACPIManager.OnInitCompleteListener mOnInitFinishListener = new DTVACPIManager.OnInitCompleteListener() {
		@Override
		public void onInitComplete(boolean isSuccess, int level) {
			
			sTest = "After onInitComplete <<<>>> Before try <<<>>> InitStatus (isSuccess) = " + isSuccess;
			iLevel = level;
			bInitSuccess = isSuccess;
			
			/*if (isSuccess) {
				sTest = "onInitComplete >>> Callback OK >>> Licença OK >>> " + bInitSuccess;
			} else {
				sTest = "onInitComplete >>> Callback OK >>> Licença INVÁLIDA >>> " + bInitSuccess;
			}*/
		}		
	};
	//End Callback inicialização DTVACPIManager*/
}