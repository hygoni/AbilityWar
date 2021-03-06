package daybreak.abilitywar.game.script;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import daybreak.abilitywar.game.games.standard.Game;
import daybreak.abilitywar.game.script.ScriptException.State;
import daybreak.abilitywar.game.script.objects.AbstractScript;
import daybreak.abilitywar.game.script.objects.setter.Setter;
import daybreak.abilitywar.game.script.types.ChangeAbilityScript;
import daybreak.abilitywar.game.script.types.LocationNoticeScript;
import daybreak.abilitywar.game.script.types.TeleportScript;
import daybreak.abilitywar.utils.Messager;
import daybreak.abilitywar.utils.ReflectionUtil.ClassUtil;
import daybreak.abilitywar.utils.database.FileUtil;
import daybreak.abilitywar.utils.thread.AbilityWarThread;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static daybreak.abilitywar.utils.Validate.notNull;

/**
 * 스크립트 관리 클래스
 *
 * @author Daybreak 새벽
 */
public class Script {

	private Script() {
	}

	private static final Messager messager = new Messager();

	private static final ArrayList<AbstractScript> scripts = new ArrayList<>();

	/**
	 * 모든 스크립트를 시작시킵니다.
	 */
	public static void RunAll(Game game) {
		if (AbilityWarThread.isGameTaskRunning()) {
			for (AbstractScript script : scripts) {
				script.Start(game);
			}
		}
	}

	/**
	 * 스크립트를 추가합니다.
	 */
	public static void AddScript(AbstractScript script) {
		if (!scripts.contains(script)) {
			scripts.add(script);
		}
	}

	/**
	 * 스크립트 폴더 안에 있는 모든 스크립트를 불러옵니다.
	 */
	public static void LoadAll() {
		scripts.clear();

		for (File file : FileUtil.newDirectory("Script").listFiles()) {
			try {
				AbstractScript script = Load(file);
				scripts.add(script);
			} catch (ScriptException ignore) {
			}
		}
	}

	private static ArrayList<ScriptRegisteration> scriptTypes = new ArrayList<>();

	/**
	 * 스크립트 등록
	 *
	 * @throws IllegalArgumentException 등록하려는 스크립트 클래스의 이름이 다른 스크립트 클래스가 이미 사용하고 있는
	 *                                  이름일 경우, 이미 등록된 스크립트 클래스일 경우
	 */
	public static void registerScript(Class<? extends AbstractScript> clazz, RequiredData<?>... requiredDatas) {
		for (ScriptRegisteration check : scriptTypes) {
			if (check.getClazz().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())) {
				messager.sendConsoleMessage(clazz.getName() + " 스크립트는 겹치는 이름이 있어 등록되지 않았습니다.");
				return;
			}
		}

		if (isRegistered(clazz)) {
			messager.sendConsoleMessage(clazz.getName() + " 스크립트는 이미 등록되었습니다.");
			return;
		}

		scriptTypes.add(new ScriptRegisteration(clazz, requiredDatas));
	}

	static {
		Script.registerScript(TeleportScript.class, new RequiredData<>("텔레포트 위치", Location.class));
		Script.registerScript(ChangeAbilityScript.class, new RequiredData<>("능력 변경 대상", ChangeAbilityScript.ChangeTarget.class));
		Script.registerScript(LocationNoticeScript.class);
	}

	public static ScriptRegisteration getRegisteration(Class<? extends AbstractScript> clazz)
			throws IllegalArgumentException, ScriptException {
		if (isRegistered(clazz)) {
			for (ScriptRegisteration sr : scriptTypes) {
				if (sr.getClazz().equals(clazz)) {
					return sr;
				}
			}

			throw new ScriptException(State.Not_Found);
		} else {
			throw new IllegalArgumentException("등록되지 않은 스크립트입니다.");
		}
	}

	public static Class<? extends AbstractScript> getScriptClass(String className) throws ClassNotFoundException {
		for (ScriptRegisteration reg : scriptTypes) {
			if (reg.getClazz().getSimpleName().equalsIgnoreCase(className)) {
				return reg.getClazz();
			}
		}

		throw new ClassNotFoundException();
	}

	public static ArrayList<String> getRegisteredScriptNames() {
		ArrayList<String> scriptNames = new ArrayList<>();
		for (ScriptRegisteration reg : scriptTypes) {
			scriptNames.add(reg.getClazz().getSimpleName());
		}
		return scriptNames;
	}

	public static boolean isRegistered(Class<? extends AbstractScript> clazz) {
		for (ScriptRegisteration check : scriptTypes) {
			if (check.getClazz().equals(clazz)) {
				return true;
			}
		}

		return false;
	}

	public static class ScriptRegisteration {

		private final Class<? extends AbstractScript> clazz;
		private final RequiredData<?>[] requiredDatas;

		public ScriptRegisteration(Class<? extends AbstractScript> clazz, RequiredData<?>... requiredDatas) {
			this.clazz = clazz;
			this.requiredDatas = requiredDatas;
		}

		public Class<? extends AbstractScript> getClazz() {
			return clazz;
		}

		public RequiredData<?>[] getRequiredDatas() {
			return requiredDatas;
		}

	}

	public static class RequiredData<T> {

		private final String key;
		private final Class<T> clazz;
		private final T defaultVaule;
		private final Class<? extends Setter<T>> setterClass;

		public RequiredData(String key, Class<T> clazz, T defaultVaule, Class<? extends Setter<T>> setterClass) {
			this.key = key;
			this.clazz = clazz;
			this.defaultVaule = defaultVaule;
			this.setterClass = setterClass;
		}

		public RequiredData(String key, Class<T> clazz, Class<? extends Setter<T>> setterClass) {
			this(key, clazz, null, setterClass);
		}

		public RequiredData(String key, Class<T> clazz, T defaultVaule) {
			this(key, clazz, defaultVaule, null);
		}

		public RequiredData(String key, Class<T> clazz) {
			this(key, clazz, null, null);
		}

		public String getKey() {
			return key;
		}

		public Class<T> getClazz() {
			return clazz;
		}

		public T getDefault() {
			return defaultVaule;
		}

		public Class<? extends Setter<T>> getSetterClass() {
			return setterClass;
		}

	}

	private static final Gson gson = new Gson();
	private static final JsonParser parser = new JsonParser();

	/**
	 * {@link AbstractScript} 저장
	 */
	public static void Save(AbstractScript script) {
		try {
			if (isRegistered(script.getClass())) {
				FileUtil.newDirectory("Script");
				File f = FileUtil.newFile("Script/" + script.getName() + ".json");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				gson.toJson(script, bw);
				bw.close();
			} else {
				Messager.sendConsoleErrorMessage("등록되지 않은 스크립트입니다.");
			}
		} catch (IOException ioException) {
			Messager.sendConsoleErrorMessage("스크립트를 저장하는 도중 오류가 발생하였습니다.");
		}
	}

	/**
	 * {@link AbstractScript} 불러오기
	 */
	public static AbstractScript Load(File file) throws ScriptException {
		try {
			if (file.exists()) {
				JsonObject object = notNull(parser.parse(new BufferedReader(new FileReader(file)))).getAsJsonObject();
				Class<?> typeClass = ClassUtil.forName(object.get("scriptType").getAsString());
				BufferedReader br = new BufferedReader(new FileReader(file));
				Object script = gson.fromJson(br, typeClass);
				br.close();

				if (script != null) {
					if (script instanceof AbstractScript) {
						return (AbstractScript) script;
					} else {
						throw new ScriptException(State.IllegalFile);
					}
				} else {
					throw new NullPointerException();
				}
			} else {
				throw new IOException();
			}
		} catch (IOException | NullPointerException | ClassNotFoundException e) {
			Messager.sendConsoleErrorMessage(ChatColor.translateAlternateColorCodes('&',
					"&e" + file.getName() + " &f스크립트를 불러오는 도중 오류가 발생하였습니다."));
			throw new ScriptException(State.Not_Loaded);
		}
	}

}
