/*
 * 작성된 날짜: 2005. 5. 5.
 *
 */
package net.kldp.junzip;

import org.apache.commons.cli.*;

/**
 * @author 손권남(kwon37xi@yahoo.co.kr)
 *  
 */
public class JUnzipMain {

    /** 사용법 */
    public static final String USAGE = "junzip [-h] [-v] [-l | -d DirectoryName] [-e encoding] ZipFile\n";

    /** 도움말 말머리 */
    public static final String HEADER = "JUnzip - MS-Windows에서 압축된 Zip 파일을 UTF-8 운영체제에서 풀기";

    /** 도움말 말꼬리 */
    public static final String FOOTER = "\n더 많은 정보를 얻으려면 http://junzip.kldp.net 을 방문해주세요.";

    /** 압축되어 있는 파일들의 이름 기본 인코딩 */
    public static final String DEFAULT_ENCODING = "MS949";

    /**
     * 프로그램 수행 시작
     * 
     * @param args
     *                  인수
     */
    public static void main(String[] args) {
        CommandLineParser parser = new BasicParser();

        Options options = new Options();

        options.addOption("h", "help", false, "이 도움말을 보여줍니다.");
        options.addOption("v", "verbose", false, "오류 내용을 자세히 보여줍니다.");
        options.addOption("e", "encoding", true, "압축된 파일 이름의 문자 인코딩 지정. 기본 "
                + DEFAULT_ENCODING);

        OptionGroup listOrExtractOptionGroup = new OptionGroup();
        Option option = new Option("l", "list", false, "압축 된 파일들의 목록을 보여줍니다. "
                + "이 옵션을 지정하지 않으면 기본적으로 압축을 풉니다.");
        listOrExtractOptionGroup.addOption(option);
        option = new Option("d", "destination", true, "압축을 풀 디렉토리를 지정합니다.");
        listOrExtractOptionGroup.addOption(option);

        options.addOptionGroup(listOrExtractOptionGroup);

        CommandLine commandLine = null;
        boolean verbose = false;
        String encoding = null;
        String destination = null;
        String zipFileNames[] = null;
        
        // 옵션 읽고 파싱
        try {
            commandLine = parser.parse(options, args);

            // 도움말 출력 요청시
            if (commandLine.hasOption('h')) {
                usage(options);
                System.exit(0);
            }

            // 오류 자세히 보기
            if (commandLine.hasOption('v')) {
                verbose = true;
            }

            // Zip 파일 목록 얻어오기
            zipFileNames = commandLine.getArgs();
            if (zipFileNames.length != 1) {
                throw new JUnzipException(JUnzipException.ETC_ERROR,
                        "Zip 파일은 1 개만 지정가능합니다.");
            }
            
        } catch (Exception ex) {
            usage(options);
            System.exit(1);
        }

        try {
            System.out.println(HEADER + "\n");


            // 인코딩 지정
            if (commandLine.hasOption('e')) {
                encoding = commandLine.getOptionValue('e');
            } else {
                encoding = DEFAULT_ENCODING;
            }

            // 압축 풀 위치 지정
            if (commandLine.hasOption('d')) {
                destination = commandLine.getOptionValue('d');
            } else {
                destination = ".";
            }

            JUnzip junzip = new JUnzip(zipFileNames[0], encoding);

            if (commandLine.hasOption('l')) {
                junzip.listEntries();
            } else {
                junzip.extractEntries(destination);
            }
        } catch (Exception ex) {
            if (verbose == true) {
                System.out
                        .println("----------------------------- 오류내용 상세 출력 -----------------------------");
                ex.printStackTrace();
                System.out
                        .println("--- 오류 원인을 모를 경우 위 내용과 옵션 기재 사항을 버그 리포트해주세요. --");
            } else {
                System.err.println("\n오류 : " + ex.getMessage());
            }
            System.exit(1);
        }
    }

    /**
     * 사용법 출력
     */
    public static void usage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
    }
}
