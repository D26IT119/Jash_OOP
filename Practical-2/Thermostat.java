public class Thermostat{
    private String location;
    private int temperature;

    private static final int MIN = 16;
    private static final int MAX = 30;
    private static int activeCount = 0;

    Thermostat(String location,int startTemp){
    this.location = location;

    if (startTemp >= MIN && startTemp <= MAX) {
        this.temperature = startTemp;
    } else {
        this.temperature = 22;
    }
    activeCount++;
    }

    Thermostat(String location){
        this(location, 22);
    }

    public void raise(){
        if (temperature < MAX){
            temperature ++;
        }
        else{
            System.out.println("Already at maximum(30)");
        }
    }

    public void lower(){
        if(temperature > MIN){
            temperature --;
        }
        else{
            System.out.println("Already at minimum(16)");
        }
    }

    public int getTemperature(){
        return temperature;
    }

    public static int getActiveCount(){
        return activeCount;
    }

    public static void main(String[] args) {
        
        Thermostat T1 = new Thermostat("Bedroom");
        Thermostat T2 = new Thermostat("Living room",25);
        System.out.println("Raising temperature of Bedroom:");

        for (int i=0 ;i<=10 ;i++){
            T1.raise();
            System.out.println("Temperature = " + T1.getTemperature());
        }

        for (int i=0; i<=20; i++){
            T1.lower();
            System.out.println("Temperature = " + T1.getTemperature());
        }
        System.out.println("Active Thermostats = " + Thermostat.getActiveCount());
    }


}
