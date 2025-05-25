package IasiTranzit.Tranzy_Iasi;

/**
 * Clasa folosita pentru a afisa informatii despre un vehicul,
 * incluzand vehiculul, ruta scurta si destinatia.
 */
public class DisplayVehicleInfo {
	/**
     * Constructor implicit pentru clasa {@code DisplayVehicleInfo}.
     * Nu initializeaza campurile.
     */
    public DisplayVehicleInfo() {
        // Constructor gol
    }
    
    /** Obiectul care contine datele vehiculului. */
    Vehicle vehicle;
    
    /** Numele scurt al rutei pe care circula vehiculul. */
    String routeShortName;
    
    /** Destinatia afisata a calatoriei vehiculului. */
    String tripHeadsign;
}