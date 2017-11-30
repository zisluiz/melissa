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
		setPosition(math.round(759+math.random(36)), 448);
		!collectHoney;
	}}.

+!move 
<-	move(right);
	.wait(20);
	!move.


+!collectHoney : pollenField4(X, Y, WIDTH, HEIGHT)
<-	for ( .range(I,Y + HEIGHT, 448)) {
        move(up);
     }
     collect(pollenField4);
   for ( .range(I,Y + HEIGHT, 448)) {
        move(down);
     }     
     delivery;
     !collectHoney.

-!collectHoney[error(ia_failed)].
-!collectHoney[error_msg(M)].

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
