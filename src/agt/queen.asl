// Agent sample_agent in project melissa

/* Initial beliefs and rules */

energia(1000).

/* Initial goals */

!startOrg("colmeia1").

/* Organisational Plans */

+!startOrg(Id)
<-  makeArtifact(Id, "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", doSimulation],SchArtId);
	debug(inspector_gui(off))[artifact_id(SchArtId)];
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
<-	registerBee(queen); 
	hiveStart.
		
/* Management Plans */		
		
+!alimentacao : goalState(_,alimentacao,_,_,satisfied)
<-	resetGoal(alimentacao).
+!renovacaoEnxame : goalState(_,renovacaoEnxame,_,_,satisfied)
<-	resetGoal(renovacaoEnxame).
+!controleTemperatura : goalState(_,controleTemperatura,_,_,satisfied)
<-	resetGoal(controleTemperatura).
		
/* Renew Plans */
		
+!porOvos[scheme(Sch)] : energia(E)  
<-	-+energia(E-5);
	createLarva;
	.wait(1000);
	!porOvos[scheme(Sch)].
	
+!comer(X) : energia(E) <-	-+energia(E+X).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }