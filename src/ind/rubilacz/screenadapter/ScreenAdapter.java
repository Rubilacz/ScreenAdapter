package ind.rubilacz.screenadapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class ScreenAdapter {
	
	private int standardWidth;
	private int standardHeight;

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	private static final String RESOURCES_HEADER = "<resources>\n";
	private static final String RESOURCES_FOOTER = "</resources>";
	private static final String NODE_PREFIX_WIDHT = "w";
	private static final String NODE_PREFIX_HEIGHT = "h";
	private static final String FILE_SCREEN_ADAPT_WIDTH = "screen_adapt_width.xml";
	private static final String FILE_SCREEN_ADAPT_HEIGHT = "screen_adapt_height.xml";
	private static final String ROOT_DIR = "./res";
	private static final String TPL_NODE = "<dimen name=\"%s%d\">%spx</dimen>\n";
	private static final String TPL_DIR_NAME = "values-%dx%d";

	public ScreenAdapter(int standardWidth, int standardHeight) {
		this.standardWidth = standardWidth;
		this.standardHeight = standardHeight;
		File rootDir = new File(ROOT_DIR);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
	}

	public void adapt(int width, int height) throws Exception {
		File fileDir = new File(ROOT_DIR + File.separator + String.format(TPL_DIR_NAME, width, height));
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		doAdapt(standardWidth, width, NODE_PREFIX_WIDHT, new File(fileDir.getAbsoluteFile(), FILE_SCREEN_ADAPT_WIDTH));
		doAdapt(standardHeight, height, NODE_PREFIX_HEIGHT, new File(fileDir.getAbsoluteFile(), FILE_SCREEN_ADAPT_HEIGHT));
	}

	private void doAdapt(int base, int value, String nodePrefix, File file) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(XML_HEADER);
		sb.append(RESOURCES_HEADER);
		float cellValue = value * 1.0f / base;
		for (int i = 1; i <= base; i++) {
			sb.append(String.format(TPL_NODE, nodePrefix, i, String.valueOf(getPx(cellValue * i))));
		}
		sb.append(RESOURCES_FOOTER);

		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		pw.print(sb.toString());
		pw.close();
	}

	public static float getPx(float px) {
		int temp = (int) (px * 100);
		return temp / 100f;
	}

	public static void main(String[] args) throws Exception {
		Ini ini = new Ini();
		ini.load(new File("dat/config.ini"));
		Section sctStandard = ini.get("standard");
		int standardWidth = Integer.parseInt(sctStandard.get("width").trim());
		int standardHeight = Integer.parseInt(sctStandard.get("height").trim());

		ScreenAdapter adapter = new ScreenAdapter(standardWidth, standardHeight);
		Section sctScreens = ini.get("screen");
		Set<String> screens = sctScreens.keySet();
		for (String screen : screens) {
			String screenDesc = sctScreens.get(screen).trim();
			String[] size = screenDesc.split(",");
			adapter.adapt(Integer.valueOf(size[0].trim()), Integer.valueOf(size[1].trim()));
		}
	}

}
