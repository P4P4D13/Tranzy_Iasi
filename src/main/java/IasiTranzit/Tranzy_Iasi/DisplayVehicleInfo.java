package IasiTranzit.Tranzy_Iasi;

/**
 * Clasa {@code DisplayVehicleInfo} encapsuleaza informatiile necesare pentru afisarea unui vehicul in interfata grafica.
 * Aceasta combină datele despre vehicul cu informații suplimentare despre rută și destinație.
 */
public class DisplayVehicleInfo {
    /** Obiectul vehicul conținând toate informațiile despre vehicul */
    public Vehicle vehicle;
    
    /** Numele scurt al rutei pe care circulă vehiculul */
    public String routeShortName;
    
    /** Destinația traseului pe care circulă vehiculul */
    public String tripHeadsign;
    
    /**
     * Constructor implicit care inițializează variabilele cu valori implicite.
     */
    public DisplayVehicleInfo() {
        vehicle = null;
        routeShortName = "N/A";
        tripHeadsign = "N/A";
    }
}