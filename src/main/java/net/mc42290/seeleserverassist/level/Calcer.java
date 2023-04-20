package net.mc42290.seeleserverassist.level;

public class Calcer {
    private static final double LOGIN_TIME_COEF = 1.0;
    private static final double LOGIN_TIME_EXPO = 1.0;
    private static final double ATTACK_DAMAGE_COEF = 1.0;
    private static final double ATTACK_DAMAGE_EXPO = 1.0;
    private static final double RECEIVE_DAMAGE_COEF = 1.0;
    private static final double RECEIVE_DAMAGE_EXPO = 1.0;

    static double bonusCoef = 1.0;

    public static long calcLv(long exp){
        return exp;  //近日改良
    }

    public static long calcExp(long loginTime,double attackDamage, double receiveDamage, long bonusExp){
        return (long)(
                (
                    LOGIN_TIME_COEF * Math.pow(loginTime,LOGIN_TIME_EXPO) +
                    ATTACK_DAMAGE_COEF * Math.pow(attackDamage,ATTACK_DAMAGE_EXPO) +
                    RECEIVE_DAMAGE_COEF * Math.pow(receiveDamage,RECEIVE_DAMAGE_EXPO)
                ) * bonusCoef + bonusExp
            ); //近日修正
    }
}
