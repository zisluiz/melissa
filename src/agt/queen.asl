// Agent sample_agent in project melissa

/* Initial beliefs and rules */

energia(1000).

/* Initial goals */

!startOrg(1).

/* Organisational Plans */

+!startOrg(Id)
<-  .concat("sch_",Id,SchName);
	makeArtifact(SchName, "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", doSimulation],SchArtId);
	debug(inspector_gui(off))[artifact_id(SchArtId)];
	.my_name(Me); setOwner(Me)[artifact_id(SchArtId)];  // I am the owner of this scheme!
	focus(SchArtId);
	addScheme(SchName);  // set the group as responsible for the scheme
	commitMission(mRainha)[artifact_id(SchArtId)];
	commitMission(mAlimentacao);
	commitMission(mRenovacao);
	commitMission(mTemperatura).
	
+!start[scheme(Sch)]                        // plan for the goal start defined in the scheme
<- 	makeArtifact("Hive", "artifact.HiveArtifact", [], HiveId); // create the hive artifact
    focus(HiveId);  // place observable properties of ArtId into a name space
    .print("Starting hive artifact");
      
  	makeArtifact("Map", "artifact.MapArtifact", [], MapId); // create the map artifact
    focus(MapId);  // place observable properties of ArtId into a name space
    .print("Starting map artifact");
	
	.wait(6000).
	
+!registerBee[scheme(Sch)]
<-	registerBee(queen, 100); 
	hiveStart.
		
/* Management Plans */		
		
+!alimentacao.
+!renovacaoEnxame.
+!controleTemperatura.
		
/* Renew Plans */
		
+!porOvos[scheme(Sch)]   
<-	createLarva;
	.wait(1000);
	!porOvos[scheme(Sch)].

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }