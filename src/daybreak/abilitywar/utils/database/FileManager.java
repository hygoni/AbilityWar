package daybreak.abilitywar.utils.database;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import daybreak.abilitywar.utils.Messager;

/**
 * File 유틸
 * 
 * @author DayBreak 새벽
 */
public class FileManager {
	// TODO: 업데이트 필요
	private FileManager() {}

	private static final Messager messager = new Messager(null);

	private static File getDataFolder() {
		return new File("plugins/AbilityWar");
	}

	private static boolean createDataFolder() {
		File Folder = getDataFolder();
		if (!Folder.exists()) {
			Folder.mkdirs();
			return true;
		} else {
			return false;
		}
	}

	public static File getFile(String File) {

		File f = new File(getDataFolder().getPath() + "/" + File);

		try {
			if (createDataFolder()) {
				messager.sendConsoleMessage(ChatColor.translateAlternateColorCodes('&',
						"&e" + getDataFolder().getPath() + "&f 폴더를 생성했습니다."));
			}

			if (!f.exists()) {
				f.createNewFile();
			}

			return f;
		} catch (IOException e) {
			messager.sendConsoleMessage(
					ChatColor.translateAlternateColorCodes('&', "&c" + f.getPath() + " 파일을 생성하지 못했습니다."));
			return null;
		}
	}

	public static File getFolder(String Folder) {

		File f = new File(getDataFolder().getPath() + "/" + Folder);

		if (createDataFolder()) {
			messager.sendConsoleMessage(
					ChatColor.translateAlternateColorCodes('&', "&e" + getDataFolder().getPath() + "&f 폴더를 생성했습니다."));
		}

		if (!f.exists()) {
			f.mkdirs();
		}

		return f;
	}

}