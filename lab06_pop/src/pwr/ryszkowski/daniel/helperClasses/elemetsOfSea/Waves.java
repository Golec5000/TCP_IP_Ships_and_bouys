package pwr.ryszkowski.daniel.helperClasses.elemetsOfSea;

public class Waves {

    private final int [][] powerOfWaves;

    public Waves(){

        powerOfWaves = new int[5][5];
        creatWave();

    }

    public void creatWave(){

        //1
        powerOfWaves[0][0] = 0;
        powerOfWaves[0][1] = 1;
        powerOfWaves[0][2] = 2;
        powerOfWaves[0][3] = 1;
        powerOfWaves[0][4] = 0;

        //2
        powerOfWaves[1][0] = 1;
        powerOfWaves[1][1] = 2;
        powerOfWaves[1][2] = 3;
        powerOfWaves[1][3] = 2;
        powerOfWaves[1][4] = 1;

        //3
        powerOfWaves[2][0] = 2;
        powerOfWaves[2][1] = 3;
        powerOfWaves[2][2] = 4;
        powerOfWaves[2][3] = 3;
        powerOfWaves[2][4] = 2;

        //4
        powerOfWaves[3][0] = 1;
        powerOfWaves[3][1] = 2;
        powerOfWaves[3][2] = 3;
        powerOfWaves[3][3] = 2;
        powerOfWaves[3][4] = 1;

        //5
        powerOfWaves[4][0] = 0;
        powerOfWaves[4][1] = 1;
        powerOfWaves[4][2] = 2;
        powerOfWaves[4][3] = 1;
        powerOfWaves[4][4] = 0;

    }

    public int[][] getPowerOfWaves() {
        return powerOfWaves;
    }
}
