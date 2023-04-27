package net.mc42290.seeleserverassist.job.level;


import net.mc42290.seeleserverassist.Util.UtilSet;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Calcer {
    private static final double LOGIN_TIME_COEF = 2.0;
    private static final double LOGIN_TIME_EXPO = 1.0;
    private static final long LOGIN_TIME_MAX = 300;
    private static final double ATTACK_DAMAGE_COEF = 1.0;
    private static final double ATTACK_DAMAGE_EXPO = 1.0;
    private static final long ATTACK_DAMAGE_MAX = Long.MAX_VALUE;
    private static final double RECEIVE_DAMAGE_COEF = 0.9;
    private static final double RECEIVE_DAMAGE_EXPO = 0.5;
    private static final long RECEIVE_DAMAGE_MAX = Long.MAX_VALUE;

    private static final double PLAYER_LV_EXPO = 0.7;

    static double bonusCoef = 1.0;

    public static long calcJobLv(long exp){
        if(exp<=0)return 0;
        double log = Math.log10(exp);
        return (long)(Math.sqrt(exp) / (log * log +1));
    }

    public static long calcPlayerLv(long[] jobLevels){
        long lv = 0;
        for(long jobLv : jobLevels)lv+=jobLv;
        return (long)Math.pow(lv,PLAYER_LV_EXPO);
    }
    public static long calcExp(long loginTime,double attackDamage, double receiveDamage, long bonusExp){
        return (long)(
                (
                    Math.min(LOGIN_TIME_COEF * Math.pow(loginTime/60000.0,LOGIN_TIME_EXPO), LOGIN_TIME_MAX) +
                    Math.min(ATTACK_DAMAGE_COEF * Math.pow(attackDamage,ATTACK_DAMAGE_EXPO), ATTACK_DAMAGE_MAX) +
                    Math.min(RECEIVE_DAMAGE_COEF * Math.pow(receiveDamage,RECEIVE_DAMAGE_EXPO), RECEIVE_DAMAGE_MAX)
                ) * bonusCoef + bonusExp
            ); //近日修正
    }



    private static final int MAX_LOOP_CNT = 300;
    private static final double EPS = 1e-3;
    public static long calcNextLvExp(long exp){
        long nextLevel =calcJobLv(exp)+1;
        double bottom = exp;
        double top = Long.MAX_VALUE;
        int loopCnt = 0;
        while (top-bottom > EPS && loopCnt++ < MAX_LOOP_CNT){
            double center = (bottom + top) / 2;
            double log = Math.log10(center);
            double centerExpLv = Math.sqrt(center)/(log * log + 1);
            if(centerExpLv > nextLevel)top = center;
            else if(centerExpLv<nextLevel)bottom = center;
            else{
                top = center;
                break;
            }
        }
        return (long)Math.ceil(top);
    }
    public static long calcNeedExp(long exp){
        return calcNextLvExp(exp)-exp;
    }
}
