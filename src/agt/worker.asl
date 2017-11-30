// Agent sample_agent in project melissa

/* Initial beliefs and rules */
// x, y, width, height
hive(649, 449, 150, 150).
pollenField1(0, 0, 150, 200).
pollenField2(0, 300, 60, 170).
pollenField3(400, 0, 40, 40).
pollenField4(759, 230, 40, 40).


/* Initial goals */

!start.

/* Plans */

+!start 
<-	.wait(3000);
	.my_name(A); 
	.delete("worker",A,N);
	if (N <= 3) {
		registerBee(feeder, 10);
	} else { if (N <= 5) {
		registerBee(sentinel, 20);
	} else {
		registerBee(worker, 50);
		setPosition(math.round(761+math.random(30)), 449);
		.wait(1000);
		!searchHoney;
	}}.

+!searchHoney : pollenField4(X, Y, WIDTH, HEIGHT) <-
	for ( .range(I,Y + HEIGHT, 450)) {
		move(up);
	}
	     
	!collectHoney;
	     
	for ( .range(J,Y + HEIGHT, 450)) {
		move(down);
	}     
	     
	!delivery;
     
 !searchHoney.

-!searchHoney[error(ia_failed)].
-!searchHoney[error_msg(M)]/* <- .print("Error: ", M) */.

+!collectHoney <- collect(pollenField4).
+!delivery <- delivery.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }