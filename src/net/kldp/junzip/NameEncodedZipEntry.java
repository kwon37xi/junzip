/*
 * 작성된 날짜: 2005. 5. 5.
 *
 * TODO 생성된 파일에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
 */
package net.kldp.junzip;

import java.io.UnsupportedEncodingException;

import net.sf.jazzlib.ZipEntry;

/**
 * @author 손권남(kwon37xi@yahoo.co.kr)
 *
 */
public class NameEncodedZipEntry implements Comparable {
	/** 지정된 문자 인코딩으로 인코딩 된 엔트리 이름 */
	private String name = null;

	/** 저장하고 있는 엔트리 */
	public ZipEntry zipEntry = null;
	
	/**
	 * ZipEntry와 각 엔트리의 파일 이름을 인코딩할 문자 인코딩을 넘겨 밭아
	 * NameEncodedZipEntry를 생성한다.
	 * 
	 * @param zipEntry ZipEntry
	 * @param encoding 파일 이름을 인코딩할 문자 인코딩
	 * @throws JUnzipException
	 */
	public NameEncodedZipEntry(ZipEntry zipEntry, String encoding) throws JUnzipException {
		this.zipEntry = zipEntry;
		
		String nameBeforeEncode = zipEntry.getName();
		try {
			name = new String(nameBeforeEncode.getBytes("latin1"), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new JUnzipException(JUnzipException.UNSUPPORTED_ENCODING,
					encoding + " 은 올바르지 않은 문자 인코딩입니다.");
		}
	}

	/**
	 * 실제 ZipEntry를 넘겨준다.
	 * @return 실제 ZipEntry
	 */
	public ZipEntry getZipEntry() {
		return this.zipEntry;
	}
	
	/**
	 * 지정된 문자 인코딩에 따라 인코딩된 ZipEntry의 이름을 가져온다.
	 * @return 지정된 문자 인코딩에 따라 인코딩된 ZipEntry의 이름
	 */
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return name;
	}

	/**
	 * ZipEntry의 이름을 기준으로 비교할 수 있게 한다.
	 */
	public int compareTo(Object o) {
		NameEncodedZipEntry comparingEntry = (NameEncodedZipEntry)o;
		return name.compareTo(comparingEntry.getName());
	}
}
