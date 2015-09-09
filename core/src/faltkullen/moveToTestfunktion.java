/*
package faltkullen;
*/
/*
 * Detta är portat från det gamla koden, och rör en enhet baserat på deras
 * longitud och lattitud information. Notera att flera variabler SAKNAS och att
 * denna kod måste anpassas för att allt ska fungera.
 *//*


public void moveLongLat() {
	double distance_km; //Avståndet mellan mål och nuvaranade position.
	double speed_km; //Hastigheten / simulation. Notera att detta är INTE enhetens hastighet per timme, utan dess hastighet inom simulationen.
	double kmPerDegree; //Antal kilometer per grad. Relevant om vi kommer att använda oss av ett longitud och lattitud baserat system.

	*/
/* Om målet är 0.0 innebär det att vi inte har något mål *//*

	if(goal.lat == 0.0 ||goal.lng == 0.0) {
		System.out.println("Zero goal in CActor::Move.");
	}

	*/
/*Om enhetens hastighet per timme är större än 0.0*//*

	if(speed_kph > 0.0) {
		dx = goal.lng - location.lng;  //goal = positionen enheten vill nå, location = positionen enheten är vid.
		dy = goal.lat - location.lat;  //på dessa två rader räknar vi ut skillnaden/ändringen mellan dessa.

		*/
/*Om skillnaden inte är 0.0 så rör vi oss, annars är vi på samma ställe.*//*

		if((dx != 0.0) || (dy != 0.0)) {
			distance_km = sqrt(dx*dx + dy*dy) * kmPerDegree; //Beräkna avståndet till målet.

			speed_km = speed_kph * sim.getHoursPerIteration(); //Hastigheten/Sträckan som enheten har.

			*/
/*Om avståndet är högre än hastigheten. *//*

			if (distance_km > speed_km) {
				portion = speed_km/distance_km; //Hur stor portion av avståndet har vi tagit igen.
				dx *= portion;
				dy *= portion;
				location.lng += dx; //addera ändring i longitud.
				location.lat += dy; //addera ändring i latitud.
			} else {
				*/
/*Vi har nått målet*//*

				location = goal; //Sätt målet som vår nuvarande lokation.
				dx = dy = 0.0; //Ändringen säts till 0;
			}
			MakeBox(); //Skapa en låda runt enheten.
		}
	} else {
		dx = dy = 0; //Inget fungerade :(
	}
}
*/
