// Agent sample_agent in project melissa

/* Initial beliefs and rules */

maxEnergia(1000).
energia(1000).
	
com_fome :-
	energia(E) &
	maxEnergia(M) &
	E <= M * 0.2.

/* Initial goals */

!startOrg("colmeia1").

/* Organisational Plans */

+!startOrg(Id)
<-  makeArtifact(Id, "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", doSimulation],SchArtId);
	debug(inspector_gui(on))[artifact_id(SchArtId)];
	.my_name(Me); setOwner(Me)[artifact_id(SchArtId)];  // I am the owner of this scheme!
	focus(SchArtId);
	.wait(200);
	addScheme(Id);  // set the group as responsible for the scheme
	adoptRole(monarca);
	commitMission(mRainha)[artifact_id(SchArtId)];
	commitMission(mAlimentacao)[artifact_id(SchArtId)];
	commitMission(mRenovacao)[artifact_id(SchArtId)];
	commitMission(mTemperatura)[artifact_id(SchArtId)].
	
+!start[scheme(Sch)]                        // plan for the goal start defined in the scheme
<- 	makeArtifact("Hive", "artifact.HiveArtifact", [], HiveId); // create the hive artifact
    focus(HiveId);  // place observable properties of ArtId into a name space
    .print("Starting hive artifact");
      
  	makeArtifact("Map", "artifact.MapArtifact", [], MapId); // create the map artifact
    focus(MapId);  // place observable properties of ArtId into a name space
    .print("Starting map artifact");
	
	.wait(6000).
	
+!registerBee[scheme(Sch)]
<-	registerBee(monarca); 
	hiveStart.
		
/* Management Plans */		
		
+!alimentacao : goalState(_,alimentacao,_,_,satisfied)
<-	resetGoal(alimentacao).
+!renovacaoEnxame : goalState(_,renovacaoEnxame,_,_,satisfied)
<-	resetGoal(renovacaoEnxame).
+!controleTemperatura : goalState(_,controleTemperatura,_,_,satisfied)
<-	resetGoal(controleTemperatura).
		
/* Renew Plans */
		
+!porOvos[scheme(Sch)] : energia(E) & not com_fome
<-	-+energia(E-5);
	createLarva;
	.wait(10000);
	!porOvos[scheme(Sch)].
	
+!comer(X) : energia(E) <-	comer(math.floor(X/2)); -+energia(E+X).
-!comer(X).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }