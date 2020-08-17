package net.kldp.junzip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TreeSet;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipFile;

/**
 * <h3>UTF-8 Linux 를 위한 한글 윈도우에서 압축된 ZIP 파일 풀기 도구</h3>
 * 
 * <h4>License</h4>
 * <p>
 * GNU General Public License 2
 * </p>
 * 
 * @version 0.3
 * @author 손권남 kwon37xi@yahoo.co.kr
 */
public class JUnzip {

    /** 읽고/쓰기 버퍼 크기 */
    private static final int BUFFER_SIZE = 1024 * 128; // 128kb

    /** Zip 파일 이름 */
    private String zipFileName = null;

    /** Zip 엔트리 파일 이름의 문자셋 */
    private String encoding = null;

    /** Zip파일 객체 */
    private ZipFile zipFile = null;

    /** 파일 이름을 알맞는 문자셋으로 인코딩한 ZipEntry */
    private Object[] zipEntries = null;

    /**
     * 생성자
     * 
     * @param filename
     *                  Zip 파일 이름
     * @throws JUnzipException
     */
    public JUnzip(String filename, String encoding) throws JUnzipException {
        File givenFile = new File(filename);

        if (!givenFile.exists()) {
            throw new JUnzipException(JUnzipException.FILE_NOT_FOUND, filename
                    + " 파일은 존재하지 않습니다.");
        } else if (!givenFile.canRead()) {
            throw new JUnzipException(JUnzipException.FILE_CANNOT_READ,
                    filename + " 파일을 읽을 수 없습니다.");
        }

        // 파일명 인코딩 지정
        this.encoding = encoding;

        try {
            zipFile = new ZipFile(givenFile);
        } catch (Exception ex) {
            throw new JUnzipException(JUnzipException.ETC_ERROR, "지정된 "
                    + filename + " 파일을 이용해 ZipFile 객체 생성에 실패하였습니다.", ex);
        }
    }

    /**
     * 지정된 ZIP 파일에서 Zip 파일의 각 압축된 엔트리들을 정렬된 목록으로 뽑아온다.
     * 
     * @throws JUnzipException
     */
    public synchronized Object[] getZipEntries() throws JUnzipException {
        // 이미 getZipEntries()가 실행됐었다면 그냥 리턴한다.
        if (this.zipEntries != null) {
            return this.zipEntries;
        }

        // 정렬상태로 ZipEntry 들을 저장한다.
        TreeSet zipEntrySet = new TreeSet();

        Enumeration zipEntriesEnum = zipFile.entries();

        // ZipEntry 들의 파일 이름의 인코딩을 바꿔서
        // NameEncodedZipEntry로 저장한다.
        while (zipEntriesEnum.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntriesEnum.nextElement();

            NameEncodedZipEntry neZipEntry = new NameEncodedZipEntry(entry,
                    encoding);
            zipEntrySet.add(neZipEntry);
        }

        // 현재 객체에 저장. 다시 읽어들이는 일이 없도록 한다.
        this.zipEntries = zipEntrySet.toArray();

        return this.zipEntries;
    }

    /**
     * 압축을 푼다.
     * 
     * @param destination
     *                  압축을 풀 대상 디렉토리를 지정한다.
     * @throws JUnzipException
     */
    public void extractEntries(String destination) throws JUnzipException {

        // 압축이 풀릴 디렉토리를 검사한다.
        File destinationFile = new File(destination);
        
        if (!destinationFile.exists()) {
            throw new JUnzipException(JUnzipException.FILE_NOT_FOUND,
                    destination + " 디렉토리가 존재하지 않습니다.");
        } else if (!destinationFile.isDirectory()) {
            throw new JUnzipException(JUnzipException.ETC_ERROR, destination + " 은 디렉토리가 아닙니다.");
        }

        if (zipEntries == null) {
            getZipEntries();
        }

        InputStream zis = null;
        FileOutputStream fos = null;

        for (int i = 0; i < zipEntries.length; i++) {

            ZipEntry currentEntry = ((NameEncodedZipEntry) zipEntries[i])
                    .getZipEntry();

            // 압축을 푼 내용이 저장될 파일 혹은 디렉토리
            String entryName = ((NameEncodedZipEntry) zipEntries[i]).getName();
            File entryFile = new File(destination + "/" + entryName);

            System.out.print(" * ");

            if (currentEntry.isDirectory()) { // 엔트리가 디렉토리이면
                System.out.println("[ Dir] \"" + entryName + "\" 디렉토리 생성");
                // 디렉토리가 존재하지 않으면 디렉토리를 생성한다.
                // 실제로 여기로 오는 경우는 없다.
                if (!entryFile.exists()) {
                    entryFile.mkdir();
                }
            } else { // 엔트리가 파일이면,
                System.out.print("[File] \"" + entryName + "\" 압축풀기 시작... ");

                // 파일명의 일부로 있는 디렉토리 이름을 뽑아서
                // 디렉토리를 미리 생성해 두어야 한다.
                String dirName = getDirectoryNameFromFileName(destination + "/"
                        + entryName);
                // 디렉토리 생성
                createAllDirectory(dirName);

                // 파일 생성
                try {
                    if (entryFile.exists() == false) {
                        boolean success = entryFile.createNewFile();
                    }
                } catch (Exception ex) {
                    throw new JUnzipException(
                            JUnzipException.CANNOT_CREATE_FILE, entryName
                                    + " 파일 생성 실패", ex);
                }

                // 생성된 파일에 압축 푼 내용 저장
                try {
                    zis = zipFile.getInputStream(currentEntry);
                    fos = new FileOutputStream(entryFile);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read = 0;

                    read = zis.read(buffer);
                    while (read >= 0) {
                        fos.write(buffer, 0, read);
                        read = zis.read(buffer);
                    }
                    System.out.println(" 끝");

                } catch (IOException ex) {
                    throw new JUnzipException(JUnzipException.ETC_ERROR,
                            "알수 없는 오류가 발생하였습니다.", ex);
                } finally {
                    try {
                        // 파일 풀기 끝
                        if (fos != null) {
                            fos.close();
                        }
                        if (zis != null) {
                            zis.close();
                        }
                    } catch (IOException e) {
                        // ignored
                    }
                } // end of try/catch/finally
            } // end of "else"
        } // end of for
    } // end of method

    /**
     * 파일 이름에 포함된 디렉토리 이름을 뽑아낸다.
     * 
     * @param fullFileName
     *                  파일 이름
     * @return 디렉토리 이름. 디렉토리 이름을 포함하지 않을 경우 null
     */
    private String getDirectoryNameFromFileName(String fullFileName) {
        int lastIdx = fullFileName.lastIndexOf("/");

        String dirName = null;

        if (lastIdx > 0) {
            dirName = fullFileName.substring(0, lastIdx);
        }

        return dirName;
    }

    /**
     * 디렉토리 이름을 받아서 디렉토리를 생성한다. 디렉토리가 여러 단계로 이뤄져 있을 경우, 필요한 모든 단계의 디렉토리를 생성한다.
     * 
     * @param dirName
     *                  디렉토리 이름
     */
    protected void createAllDirectory(String dirName) {
        if (dirName != null) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * Zip 파일의 목록을 출력한다.
     * 
     * @throws JUnzipException
     */
    public void listEntries() throws JUnzipException {

        if (zipEntries == null) {
            getZipEntries();
        }

        for (int i = 0; i < zipEntries.length; i++) {
            ZipEntry currentEntry = ((NameEncodedZipEntry) zipEntries[i])
                    .getZipEntry();
            String entryName = ((NameEncodedZipEntry) zipEntries[i]).getName();

            if (currentEntry.isDirectory()) {
                System.out.print(" [ Dir] ");
            } else {
                System.out.print(" [File] ");
            } // end of if/else

            System.out.println(entryName);
        } // end of for
    }
}
