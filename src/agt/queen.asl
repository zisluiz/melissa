// Agent sample_agent in project melissa

/* Initial beliefs and rules */

/* Initial goals */

!startOrg(1).

/* Plans */

/*+!start 
<-  .wait(3000); 
	registerBee(queen, 100); 
	honeyStart(100);
	//!startOrg;
	!createLarva. */

+!startOrg(Id)
<-  .concat("sch_",Id,SchName);
	makeArtifact(SchName, "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", doSimulation],SchArtId);
	debug(inspector_gui(on))[artifact_id(SchArtId)];
	.my_name(Me); setOwner(Me)[artifact_id(SchArtId)];  // I am the owner of this scheme!
	focus(SchArtId);
	addScheme(SchName);  // set the group as responsible for the scheme
	commitMission(mRainha)[artifact_id(SchArtId)].
	
+!start[scheme(Sch)]                        // plan for the goal start defined in the scheme
<- 	makeArtifact("Hive", "artifact.HiveArtifact", [], HiveId); // create the auction artifact
    focus(HiveId);  // place observable properties of ArtId into a name space
    .print("Starting hive artifact");
      
  	makeArtifact("Map", "artifact.MapArtifact", [], MapId); // create the auction artifact
    focus(MapId);  // place observable properties of ArtId into a name space
    .print("Starting map artifact");
	
	.wait(6000).
	
+!registerBee[scheme(Sch)]
<-	registerBee(queen, 100); 
	honeyStart(100).
		
      
+!porOvos[scheme(Sch)]   
<-	createLarva;
	.wait(1000);
	!porOvos[scheme(Sch)].

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }