package io.github.xxyy.minotopiacore.helper;

import io.github.xxyy.minotopiacore.Const;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;


public class MTCFormatter extends Formatter {
    
    @Override
    public String format(LogRecord rec) {
        return "["+new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:S").format(rec.getMillis())+"|"+rec.getLevel()+"@"+rec.getLoggerName()+"] "+rec.getMessage()+
                "  {@"+rec.getSourceMethodName()+"}\n";
    }
    
    @Override
    public String getHead(Handler h){
        return "******************** MTC LOG FILE ********************\n" +
        		" * Date: "+(new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().getTimeInMillis())+"\n" +
        		" * Bukkit Version: "+Bukkit.getVersion()+"\n" +
        		" * Plugin Version: "+Const.versionString+"\n" +
        		" * Encoding: "+h.getEncoding()+"\n" +
        		" * Formatter: MTCFormatter\n" +
        		"******************** MTC LOG FILE ********************\n");
    }
    
}
