// Agent sample_agent in project melissa

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start <- .wait(3000); registerBee(queen, 100); honeyStart(100); !createLarva.

+!createLarva <- createLarva; .wait(1000); !createLarva.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$jacamoJar/templates/org-obedient.asl") }
