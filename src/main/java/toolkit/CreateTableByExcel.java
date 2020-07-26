package toolkit;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import static toolkit.DBTool.getConnection;

/**
 * 用于将execl中的表结构转化成建表语句并执行
 */
public class CreateTableByExcel {
    //数据库配置
    public static Connection conn;
    //execl
    private static final String URL = "src\\main\\java\\toolkit\\DB数据库配置.xlsx";
    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DBURI = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
    private static final String USER = "item_pmp";
    private static final String PASS = "item_pmp";



    public static Connection initConn(){
        return getConnection(DRIVER,DBURI ,USER ,PASS );
    }

    public static Connection initConn(String driver,String url,String user,String pass){
        if(driver!=null&&url!=null&&user!=null&&pass!=null)
        return getConnection(driver, url, user, pass);
        else
            return initConn();
    }

    /**
     * 初始化execl
     * @return wb：execl对象
     * @throws IOException
     */

    public static Workbook initWorkBook(String FilePath) throws IOException {
        String url;
        if(FilePath!=null)
            url=FilePath;
        else url=URL;
        File file = new File(url);
        InputStream is = new FileInputStream(file);

        Workbook workbook = null;
        //根据后缀，得到不同的Workbook子类，即HSSFWorkbook或XSSFWorkbook
        if (url.endsWith(SUFFIX_2003)) {
            workbook = new HSSFWorkbook(is);
        } else if (url.endsWith(SUFFIX_2007)) {
            workbook = new XSSFWorkbook(is);
        }

        return workbook;
    }

    public static Workbook initWorkBook() throws IOException {
        return initWorkBook(null);
    }

    /**
     * 创建表的具体操作,并导出sql脚本
     * @param conn
     * @param tableBaseName
     * @param sqlString
     */
    public static void createTable(Connection conn, String tableBaseName, String sqlString,String userName) {
        try {
            ResultSet rs1 = conn.createStatement().executeQuery("select * from "+userName+"." + tableBaseName);
            System.out.println( tableBaseName + "表已存在");
            //conn.createStatement().execute("DROP table "+userName+"."+tableBaseName);
            //System.out.println("表删除成功："+tableBaseName);
        } catch (SQLException s1) {
            try {
                FileWriter fw=new FileWriter(new File("src\\main\\java\\toolkit\\OUTPUT_SQL.sql"),true);
                System.out.println("表不存在，进行创建");
                System.out.println("CREATE table "+userName+"." + tableBaseName + sqlString);
                conn.createStatement().execute("CREATE table "+userName+"." + tableBaseName + sqlString);
                System.out.println("表创建成功："+userName+"." + tableBaseName);
                fw.write("CREATE table "+userName+"." + tableBaseName + sqlString+";");
                fw.write("\n");
                fw.flush();
                fw.close();
                //用于测试，将表删掉
                conn.createStatement().execute("DROP table "+userName+"."+tableBaseName);
                System.out.println("表删除成功："+tableBaseName);
            } catch (SQLException s2) {
                System.out.println("创建失败");
                System.out.println(s2.getCause());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取数据结构并将其转换为sql的一部分
     * @param sheet
     * @return
     */
    public static String getDATA(Sheet sheet) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        sb.append("(");
        //(data_id varchar(70) )
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(2) == null || "".equals(row.getCell(2).getStringCellValue())) break;

            //对于一些具体值的替换，可考虑封装出来
            if ("LEVEL".equals(row.getCell(1).getStringCellValue().toUpperCase())) {
                sb.append("LEVEL_NO ");
            } else {
                sb.append(replaceBlank(row.getCell(1).getStringCellValue()) + " ");
            }

            //对于数据类型的转化
            if ("DATE".equals(row.getCell(3).getStringCellValue().toUpperCase())) {
                sb.append(" VARCHAR(8) ");
            } else if ("INT".equals(row.getCell(3).getStringCellValue().toUpperCase())) {
                sb.append(" NUMBER ");
            } else if ("FLOAT".equals(row.getCell(3).getStringCellValue().toUpperCase())) {
                sb.append(" NUMBER ");
            } else if ("DATETIME".equals(row.getCell(3).getStringCellValue().toUpperCase())) {
                sb.append(" VARCHAR(20) ");
            } else if (row.getCell(3).getStringCellValue().toUpperCase().contains("DATETIME")) {
                sb.append(" VARCHAR(20) ");
            } else if (row.getCell(3).getStringCellValue().toUpperCase().contains("CLOB")) {
                sb.append(" CLOB ");
            } else if (row.getCell(3).getStringCellValue().toUpperCase().contains("BLOB")) {
                sb.append(" BLOB ");
            }else {
                sb.append(row.getCell(3).getStringCellValue().toUpperCase() + "(");
                sb.append((int) row.getCell(4).getNumericCellValue() + ")");
            }

            if ("Y".equals(row.getCell(5).getStringCellValue()))
                sb.append(" primary key");
            sb.append(",");
            count++;
        }
        System.out.println(count);
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }


    /**
     * 获取数据，并以表名为key，数据为value
     * @return
     */
    public static List<Map<String, String>> getDATAMap() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            Workbook wb = initWorkBook();
            //获取sheet数
            int sheetNum = wb.getNumberOfSheets();
            Scanner sc = new Scanner(System.in);
            //System.out.println("请输入起始sheet序号（从0开始）");
            //int startSheet=sc.nextInt();
            int startSheet = 1;
            if (startSheet + 1 > sheetNum) {
                System.out.println("输入有误");
                System.exit(0);
            }
            for (int i = startSheet; i < sheetNum; i++) {
                System.out.println("开始处理sheet页：" + wb.getSheetName(i));
                //System.out.println(i);
                Sheet sheet = wb.getSheetAt(i);
                Row row = sheet.getRow(sheet.getFirstRowNum() + 1);
                Cell cell = row.getCell(row.getFirstCellNum() + 1);
                String tableBaseName = cell.getStringCellValue();
                tableBaseName = replaceBlank(tableBaseName);
                System.out.println("位数" + tableBaseName.length());
                String data = getDATA(sheet);
                Map<String, String> map = new HashMap<>();
                map.put(tableBaseName, data);
                list.add(map);
                System.out.println("获取数据完成");
            }
        } catch (Exception e) {
            System.out.println(e.getCause());

        }
        return list;
    }

    /**
     * 方法主入口
     * 主要参数为文件路径和数据库配置，为选填项
     */
    public static void mainProc() {
        mainProc(null,null,null,null);
    }

    public static void mainProc(String driver,String url,String user,String pass) {
        conn=initConn(driver,url,user,pass);
        List<Map<String, String>> list = getDATAMap();
        int count = 0;
        for (Map<String, String> table : list) {
            for (String key : table.keySet()) {
                System.out.println(++count);
                System.out.println("-------------------" + key + "处理开始------------------------");
                createTable(conn,key, table.get(key),user==null?USER:user);
                System.out.println("-------------------" + key + "处理结束------------------------");

            }
        }
        System.out.println(list.size());
        try {
            conn.rollback();
        } catch (Exception e) {
            e.getCause();
        }
    }

    public static void preCheck() {
        Map<String, String> map = new HashMap<>();
        try {
            Workbook wb = initWorkBook();
            //获取sheet数
            int sheetNum = wb.getNumberOfSheets();
            Scanner sc = new Scanner(System.in);
            //System.out.println("请输入起始sheet序号（从0开始）");
            //int startSheet=sc.nextInt();
            int startSheet = 1;
            if (startSheet + 1 >= sheetNum) {
                System.out.println("输入有误");
                System.exit(0);
            }
            for (int i = startSheet; i < sheetNum; i++) {
                System.out.println("开始处理sheet页：" + wb.getSheetName(i));
                //System.out.println(i);
                Sheet sheet = wb.getSheetAt(i);
                for (int j = 3; j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row == null || row.getCell(2) == null || "".equals(row.getCell(2).getStringCellValue())) break;

                    //System.out.println(row.getCell(3));
                    map.put(row.getCell(3).getStringCellValue(), sheet.getRow(1).getCell(0).getStringCellValue() + row.getCell(2).getStringCellValue());
                }
                System.out.println("扫描结束");
            }
            for (String key : map.keySet()) {
                System.out.println(key + ":" + map.get(key));
            }
        } catch (Exception e) {
            System.out.println(e.getCause());

        }
    }


    public static String replaceBlank(String s) {
        return (s.trim()).replace(" ", "_");
    }


    public static void main(String[] args) {
        mainProc();

    }

}
