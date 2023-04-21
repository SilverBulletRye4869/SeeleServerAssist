package net.mc42290.seeleserverassist.job.level;


import org.bukkit.entity.Player;

import java.util.UUID;

public class Calcer {
    private static final double LOGIN_TIME_COEF = 1.0;
    private static final double LOGIN_TIME_EXPO = 1.0;
    private static final double ATTACK_DAMAGE_COEF = 1.0;
    private static final double ATTACK_DAMAGE_EXPO = 1.0;
    private static final double RECEIVE_DAMAGE_COEF = 1.0;
    private static final double RECEIVE_DAMAGE_EXPO = 1.0;

    private static final double JOB_LV_EXPO = 0.8;
    private static final double PLAYER_LV_EXPO = 0.6;

    static double bonusCoef = 1.0;

    public static long calcJobLv(long exp){
        return (long)Math.pow(exp,JOB_LV_EXPO);  //近日改良
    }

    public static long calcPlayerLv(Player p, long[] jobLevels){return calcPlayerLv(p.getUniqueId(),jobLevels);}
    public static long calcPlayerLv(UUID uuid, long[] jobLevels){
        long lv = 0;
        for(long jobLv : jobLevels)lv+=jobLv;
        return (long)Math.pow(lv,PLAYER_LV_EXPO);
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
