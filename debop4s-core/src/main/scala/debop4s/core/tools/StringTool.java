package debop4s.core.tools;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import debop4s.core.BinaryStringFormat;
import debop4s.core.utils.Codecs;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.String.format;

/**
 * String 관련 Helper Class 입니다.
 * {@link org.apache.commons.lang3.StringUtils} 를 보완합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오후 9:13
 * @deprecated use {@link debop4s.core.utils.Strings}
 */
@Deprecated
public abstract class StringTool {

    private StringTool() {}

    private static final Logger log = LoggerFactory.getLogger(StringTool.class);

    /**
     * 멀티바이트 문자열을 바이트 배열로 변환 시에 선두번지에 접두사로 넣는 값입니다.
     * 이 값이 있으면 꼭 UTF-8 으로 변환해야 한다는 뜻입니다.
     */
    protected static final byte[] MULTI_BYTES_PREFIX = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    /** The constant TRIMMING_STR. */
    public static final String TRIMMING_STR = "...";

    /** NULL 을 표현하는 문자열 */
    public static final String NULL_STR = "<null>";

    /** 빈 문자열 */
    public static final String EMPTY_STR = "";

    /** The constant COMMA String. */
    public static final String COMMA_STR = ",";

    /** The constant UTF8. */
    public static final Charset UTF8 = Charsets.UTF_8;

    public static String asString(Object x) {
        return (x == null) ? NULL_STR : x.toString();
    }

    //region << isNull / isEmpty / isWhitespace / isMultiByteString >>

    /**
     * 문자열이 null 이면 true 를, null 이 아니면 false 를 반환한다.
     *
     * @param str 문자열
     * @return 문자열이 null 이면 true, 아니면 false
     */
    public static boolean isNull(final String str) {
        return (str == null);
    }

    /**
     * 문자열이 null 이 아니라면 true를 반환하고, null 이라면 false를 반환한다.
     *
     * @param str 문자열
     * @return the boolean
     */
    public static boolean isNotNull(final String str) {
        return (str != null);
    }

    /**
     * 문자열이 null 이거나 길이가 0인 빈 문자열인가?
     *
     * @param str 문자열
     * @return 빈 문자열이면 true, 아니면 false
     */
    public static boolean isEmpty(final String str) {
        return isEmpty(str, true);
    }

    /**
     * 문자열이 null 이거나 trim을 수행한 후 길이가 0인 빈 문자열인가?
     *
     * @param str      문자열
     * @param withTrim String#trim() 수행 여부
     * @return 빈 문자열이면 true, 아니면 false
     */
    public static boolean isEmpty(final String str, final boolean withTrim) {
        return isNull(str) || (((withTrim) ? str.trim() : str).isEmpty());
    }

    /**
     * 지정한 문자열이 빈 문자열 아니면 true, 빈 문자열이면 false
     *
     * @param str 문자열
     * @return 빈 문자열이면 false, 아니면 true
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    /**
     * 지정한 문자열이 빈 문자열 아니면 true, 빈 문자열이면 false
     *
     * @param str      문자열
     * @param withTrim 문자열에 trim 을 수행할 것인가?
     * @return 빈 문자열이면 false, 아니면 true
     */
    public static boolean isNotEmpty(final String str, final boolean withTrim) {
        return !isEmpty(str, withTrim);
    }

    /**
     * 지정한 문자열이 의미있는 문자열이 아닌 char (공백, \t, \b, \r, \n 등) 만 있다면 true, 아니면 false
     *
     * @param str 문자열
     * @return 문자열에 보이는 값이 있으면 false, 아니면 true
     */
    public static boolean isWhitespace(final String str) {
        return (str == null) || StringUtils.isWhitespace(str);
    }

    /**
     * Is not white space.
     *
     * @param str 문자열
     * @return the boolean
     */
    public static boolean isNotWhitespace(final String str) {
        return (str != null) && !StringUtils.isWhitespace(str);
    }

    /**
     * Is multi byte string.
     *
     * @param bytes the bytes
     * @return the boolean
     */
    public static boolean isMultiByteString(final byte[] bytes) {
        if (bytes == null || bytes.length < MULTI_BYTES_PREFIX.length)
            return false;

        return Arrays.equals(MULTI_BYTES_PREFIX,
                             Arrays.copyOf(bytes, MULTI_BYTES_PREFIX.length));
    }

    /**
     * 문자열이 멀티바이트 (한글,일어,중국어 등 2바이트 이상의 언어) 인가 확인한다.
     *
     * @param str 문자열
     * @return 멀티바이트 언어라면 true, 아니면 false
     */
    public static boolean isMultiByteString(final String str) {
        if (isWhitespace(str))
            return false;

        try {
            final String prefix = str.substring(0, Math.min(2, str.length()));
            byte[] bytes = prefix.getBytes(Charsets.US_ASCII);
            return isMultiByteString(bytes);
        } catch (Exception e) {

            log.error("멀티바이트 문자열인지 확인하는데 실패했습니다. str=" + ellipsisChar(str, 24), e);
            return false;
        }
    }

    /**
     * Contains boolean.
     *
     * @param str    the str
     * @param subStr the sub str
     * @return the boolean
     */
    public static boolean contains(final String str, final String subStr) {
        return str.contains(subStr);
    }

    //endregion

    // region << ellipsis >>


    /**
     * Need ellipsis.
     *
     * @param str       the str
     * @param maxLength the max length
     * @return the boolean
     */
    public static boolean needEllipsis(final String str, final int maxLength) {
        return isNotEmpty(str) && str.length() > maxLength;
    }

    /**
     * Ellipsis char.
     *
     * @param str       the str
     * @param maxLength the max length
     * @return the string
     */
    public static String ellipsisChar(final String str, final int maxLength) {
        if (isEmpty(str) || !needEllipsis(str, maxLength))
            return str;

        return str.substring(0, maxLength - TRIMMING_STR.length()) + TRIMMING_STR;
    }

    /**
     * Ellipsis path.
     *
     * @param str       the str
     * @param maxLength the max length
     * @return the string
     */
    public static String ellipsisPath(final String str, final int maxLength) {
        if (isEmpty(str) || !needEllipsis(str, maxLength))
            return str;

        int length = maxLength / 2;

        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(0, length))
               .append(TRIMMING_STR);

        if (maxLength % 2 == 0)
            builder.append(str.substring(str.length() - length));
        else
            builder.append(str.substring(str.length() - length - 1));

        return builder.toString();
    }

    /**
     * Ellipsis first.
     *
     * @param str       the str
     * @param maxLength the max length
     * @return the string
     */
    public static String ellipsisFirst(final String str, final int maxLength) {
        if (isEmpty(str) || !needEllipsis(str, maxLength))
            return str;

        return TRIMMING_STR + str.substring(str.length() - maxLength);
    }

    //endregion

    //region << encoding string - hex decimal / base64 >>

    /**
     * Int to hex.
     *
     * @param n the n
     * @return the char
     */
    public static char intToHex(final int n) {
        if (n <= 9)
            return (char) (n + 48);
        return (char) (n - 10 + 97);
    }

    /**
     * Hex to int.
     *
     * @param h the h
     * @return the int
     */
    public static int hexToInt(final char h) {
        if (h >= '0' && h <= '9')
            return h - '0';
        if (h >= 'a' && h <= 'f')
            return h - 'a' + 10;
        if (h >= 'A' && h <= 'F')
            return h - 'A' + 10;

        return -1;
    }

    /**
     * 16 진수로 표현된 데이타를 바이트 배열로 변환합니다.
     *
     * @param hexString 16진수로 표현된 문자열
     * @return 16 진수 바이트 배열
     */
    public static byte[] getBytesFromHexString(final String hexString) {
        if (isEmpty(hexString))
            return new byte[0];

        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {

            log.error("16진수로 표현된 문자열을 바이트 배열로 변환하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 데이터를 16 진수로 표현합니다.
     *
     * @param bytes 바이트 배열
     * @return 바이트를 16진수로 표현한 문자열
     */
    public static String getHexString(final byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    /**
     * Encode base 64.
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] encodeBase64(final byte[] input) {
        return Codecs.encodeBase64(input);
    }

    /**
     * 문자열을 Base64 형식으로 인코딩을 합니다.
     *
     * @param input the input
     * @return the string
     */
    public static String encodeBase64String(final byte[] input) {
        return getUtf8String(encodeBase64(input));
    }

    public static String encodeBase64String(final String inputString) {
        return encodeBase64String(getUtf8Bytes(inputString));
    }

    /**
     * Decode base 64.
     *
     * @param base64Data the base 64 data
     * @return the byte [ ]
     */
    public static byte[] decodeBase64(final byte[] base64Data) {
        return Base64.decodeBase64(base64Data);
    }

    /**
     * Decode base 64.
     *
     * @param base64String the base 64 string
     * @return the byte [ ]
     */
    public static byte[] decodeBase64(final String base64String) {
        return Base64.decodeBase64(base64String);
    }

    /**
     * Decode base 64 string.
     *
     * @param base64Data the base 64 data
     * @return the string
     */
    public static String decodeBase64String(final byte[] base64Data) {
        return getUtf8String(decodeBase64(base64Data));
    }

    /**
     * Decode base 64 string.
     *
     * @param base64String the base 64 string
     * @return the string
     */
    public static String decodeBase64String(final String base64String) {
        return getUtf8String(decodeBase64(base64String));
    }

    /**
     * 문자열을 UTF8 인코딩 방식으로 바이트 배열로 변환합니다.
     * null 이거나 빈 문자열인 경우는 byte[0] 를 반환합니다.
     *
     * @param str 문자열
     * @return 바이트 배열
     */
    public static byte[] getUtf8Bytes(final String str) {
        return (str == null) ? null : str.getBytes(UTF8);
    }

    /**
     * 바이트 배열을 UTF-8 문자열로 변환합니다.
     *
     * @param bytes the bytes
     * @return UTF-8 문자열
     */
    public static String getUtf8String(final byte[] bytes) {
        if (bytes == null) return null;
        return new String(bytes, UTF8);
    }

    /**
     * Gets string.
     *
     * @param bytes       the bytes
     * @param charsetName the charset name
     * @return the string
     */
    public static String getString(final byte[] bytes, final String charsetName) {
        if (isEmpty(charsetName))
            return getUtf8String(bytes);

        return new String(bytes, Charset.forName(charsetName));
    }

    /**
     * Gets string from bytes.
     *
     * @param bytes  the bytes
     * @param format the format
     * @return the string from bytes
     */
    public static String getStringFromBytes(final byte[] bytes, final BinaryStringFormat format) {
        return format == BinaryStringFormat.HexDecimal
                ? getHexString(bytes)
                : encodeBase64String(bytes);
    }

    /**
     * Get bytes from string.
     *
     * @param str    the str
     * @param format the format
     * @return the byte [ ]
     */
    public static byte[] getBytesFromString(final String str, final BinaryStringFormat format) {
        try {
            return format == BinaryStringFormat.HexDecimal
                    ? getBytesFromHexString(str)
                    : decodeBase64(str);
        } catch (Exception e) {
            log.error("문자열로부터 Byte[] 를 얻는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    // endregion

    // region << String manipulation >>

    /**
     * Delete char any.
     *
     * @param str   the str
     * @param chars the chars
     * @return the string
     */
    public static String deleteCharAny(final String str, final char... chars) {
        if (isEmpty(str))
            return str;

        StringBuilder builder = new StringBuilder();
        final List<Character> charList = Chars.asList(chars);

        char[] strArray = str.toCharArray();
        for (char c : strArray) {
            if (!charList.contains(c))
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Delete char.
     *
     * @param str   the str
     * @param chars the chars
     * @return the string
     */
    public static String deleteChar(final String str, final char[] chars) {
        if (isEmpty(str))
            return str;

        StringBuilder builder = new StringBuilder();
        final List<Character> charList = Chars.asList(chars);

        char[] strArray = str.toCharArray();
        for (char c : strArray) {
            if (!charList.contains(c))
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Delete char.
     *
     * @param str 문자열
     * @param dc  the dc
     * @return the string
     */
    public static String deleteChar(final String str, final char dc) {
        if (isEmpty(str))
            return str;

        StringBuilder builder = new StringBuilder();

        char[] strArray = str.toCharArray();
        for (char c : strArray) {
            if (c != dc)
                builder.append(c);
        }
        return builder.toString();
    }

    public static String concat(final Object... items) {
        StringBuilder builder = new StringBuilder();
        for (Object item : items) {
            builder.append(asString(item));
        }
        return builder.toString();
    }

    public static String concat(final String separator, final Object... items) {
        return join(items, separator);
    }

    /**
     * 아이템들을 문자열로 조합합니다.
     *
     * @param items the items
     * @return the string
     */
    public static String join(final Object... items) {
        return join(items, ",");
    }

    /**
     * 아이템들을 문자열로 조합합니다.
     *
     * @param items     the items
     * @param separator the separator
     * @return the string
     */
    public static String join(final Object[] items, final String separator) {
        if (items == null || items.length == 0) return "";

        StringBuilder builder = new StringBuilder(items.length);
        boolean first = true;
        for (Object item : items) {
            if (!first)
                builder.append(separator);
            builder.append(asString(item));
            first = false;
        }
        return builder.toString();
    }

    /**
     * Join string.
     *
     * @param strs the strs
     * @return the string
     */
    public static String join(final Iterable<?> strs) {
        return join(strs, COMMA_STR);
    }

    /**
     * Join string.
     *
     * @param items     the items
     * @param separator the separator
     * @return the string
     */
    public static String join(final Iterable<?> items, final String separator) {
        if (items == null)
            return "";

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (!first)
                builder.append(separator);
            builder.append(item);
            first = false;
        }
        return builder.toString();
    }


    /**
     * Quoted str.
     *
     * @param str 문자열
     * @return the string
     */
    public static String quotedStr(final String str) {
        return isNull(str) ? NULL_STR : format("\'%s\'", str.replace("\'", "\'\'"));
    }

    /**
     * Quoted str.
     *
     * @param str        the str
     * @param defaultStr the default str
     * @return the string
     */
    public static String quotedStr(final String str, final String defaultStr) {
        return isWhitespace(str) ? quotedStr(defaultStr) : quotedStr(str);
    }

    /**
     * 문자열을 역순으로 배열합니다.
     *
     * @param str 문자열
     * @return the string
     */
    public static String reverse(final String str) {
        if (isEmpty(str))
            return str;

        char[] cs = new char[str.length()];
        for (int i = cs.length - 1, j = 0; i >= 0; i--, j++) {
            cs[i] = str.charAt(j);
        }
        return new String(cs);
    }

    /**
     * 문자열을 n 만큼 반복해서 복사합니다.
     *
     * @param str 문자열
     * @param n   the n
     * @return the string
     */
    public static String replicate(final String str, int n) {
        return com.google.common.base.Strings.repeat(str, n);
    }

    /**
     * 문자열을 구분자로 분리해서 리스트로 만듭니다.
     *
     * @param str        the str
     * @param separators the separators
     * @return the iterable
     */
    public static Iterable<String> split(final String str, final char... separators) {
        if (isEmpty(str))
            return Lists.newArrayList();

        List<String> result = Lists.newArrayList();
        List<Character> seps = Chars.asList(separators);

        int length = str.length();
        int startIndex = 0;
        for (int i = 0; i < length; i++) {
            if (seps.contains(str.charAt(i))) {
                if (i - 1 - startIndex > 0) {
                    result.add(str.substring(startIndex, i - 1));
                }
                startIndex = i + 1;
            }
        }

        return result;
    }

    /**
     * 문자열을 구분자로 분리해서 리스트로 만듭니다.
     *
     * @param str        the str
     * @param separators the separators
     * @return the list
     */
    public static List<String> split(final String str, final String... separators) {
        return split(str, true, separators);
    }

    /**
     * 문자열을 구분자로 분리해서 리스트로 만듭니다.
     *
     * @param str        the str
     * @param ignoreCase the ignore case
     * @param separators the separators
     * @return the list
     */
    public static List<String> split(final String str, final boolean ignoreCase, final String... separators) {
        return split(str, ignoreCase, true, separators);
    }

    /**
     * 지정된 문자열을 구분자로 분리하여 배열로 반환합니다.
     *
     * @param str                the str
     * @param ignoreCase         the ignore case
     * @param removeEmptyEntries the remove empty entries
     * @param separators         the separators
     * @return the list
     */
    public static List<String> split(final String str,
                                     final boolean ignoreCase,
                                     final boolean removeEmptyEntries,
                                     final String... separators) {
        if (isEmpty(str))
            return Lists.newArrayList();

        List<String> result = Lists.newArrayList();
        List<char[]> seps = Lists.newArrayList();

        for (String sep : separators) {
            if (ignoreCase) seps.add(sep.toLowerCase().toCharArray());
            else seps.add(sep.toCharArray());
        }

        char[] strArray = str.toCharArray();
        char[] strArray2 = (ignoreCase) ? str.toLowerCase().toCharArray() : str.toCharArray();

        int startIndex = 0;
        int prevIndex = 0;
        while (startIndex < strArray.length) {
            for (char[] sep : seps) {
                if (Arrays.equals(sep, Arrays.copyOfRange(strArray2, startIndex, startIndex + sep.length))) {
                    String item = new String(Arrays.copyOfRange(strArray, prevIndex, startIndex));
                    if (!(removeEmptyEntries && isWhitespace(item)))
                        result.add(item);
                    prevIndex = startIndex + sep.length;
                    startIndex = startIndex + sep.length;
                }
            }
            startIndex++;
        }
        if (prevIndex < strArray.length - 1)
            result.add(new String(Arrays.copyOfRange(strArray, prevIndex, strArray.length)));

        return result;
    }

    // endregion

    // region << WordCount, FirstOf, LastOf >>

    /**
     * Word count.
     *
     * @param str  the str
     * @param word the word
     * @return the int
     */
    public static int wordCount(final String str, final String word) {
        return wordCount(str, word, false);
    }

    /**
     * Word count.
     *
     * @param str        the str
     * @param word       the word
     * @param ignoreCase the ignore case
     * @return the int
     */
    public static int wordCount(final String str, final String word, final boolean ignoreCase) {

        if (isEmpty(str) || isEmpty(word))
            return 0;

        final String targetStr = (ignoreCase) ? str.toUpperCase() : str;
        final String searchWord = (ignoreCase) ? word.toUpperCase() : word;

        int wordLength = searchWord.length();
        int maxLength = targetStr.length() - wordLength;

        int count = 0;
        int index = 0;
        while (index >= 0 && index <= maxLength) {

            index = targetStr.indexOf(searchWord, index);

            if (index > 0) {
                count++;
                index += wordLength;
            }
        }
        return count;
    }

    /**
     * Gets first line.
     *
     * @param str 문자열
     * @return the first line
     */
    public static String getFirstLine(final String str) {
        if (isEmpty(str))
            return str;

        int index = str.indexOf('\n');
        if (index > 0)
            return str.substring(0, index - 1);

        return str;
    }

    /**
     * Gets between.
     *
     * @param text  the text
     * @param start the start
     * @param end   the end
     * @return the between
     */
    public static String getBetween(final String text, final String start, final String end) {
        if (isEmpty(text))
            return text;

        int startIndex = 0;
        if (!isEmpty(start)) {
            int index = text.indexOf(start);
            if (index > -1) {
                startIndex = index + start.length();
            }
        }

        int endIndex = text.length() - 1;
        if (!isEmpty(end)) {
            int index = text.lastIndexOf(end);
            if (index > -1)
                endIndex = index - 1;
        }

        if (endIndex > startIndex)
            return text.substring(startIndex, endIndex);

        return EMPTY_STR;
    }

    // endregion

    // region  << objectToString, listToString, mapToString >>

    /**
     * 객체의 필드 정보를 이용하여, 객체를 문자열로 표현합니다.  @param obj the obj
     *
     * @param obj the obj
     * @return the string
     */
    public static String objectToString(final Object obj) {
        if (obj == null)
            return NULL_STR;

        com.google.common.base.MoreObjects.ToStringHelper helper
                = com.google.common.base.MoreObjects.toStringHelper(obj);

        try {
            Class objClazz = obj.getClass();
            Field[] fields = objClazz.getFields();

            for (Field field : fields)
                helper.add(field.getName(), field.get(obj));

        } catch (IllegalAccessException ignored) {
            log.warn("필드 정보를 얻는데 실패했습니다.", ignored);
        }
        return helper.toString();
    }

    /**
     * {@link Iterable} 정보를 문자열로 표현합니다.  @param items the items
     *
     * @param items the items
     * @return the string
     */
    public static <T> String listToString(final Iterable<? extends T> items) {
        return items == null ? NULL_STR : join(items, COMMA_STR);
    }

    /**
     * 객체 배열 정보를 문자열로 표현합니다.
     *
     * @param items the items
     * @return the string
     */
    public static String listToString(final Object[] items) {
        return items == null || items.length == 0 ? NULL_STR : join(items, COMMA_STR);
    }

    /**
     * {@link Map} 정보를 문자열로 표현합니다.
     *
     * @param map the map
     * @return map 정보를 표현한 문자열
     */
    public static String mapToString(final Map map) {
        return mapToString(map, "{", COMMA_STR, "}");
    }

    public static String mapToString(final Map map, final String openStr, final String delimiter, final String closeStr) {
        return map == null ? NULL_STR : openStr + join(mapToEntryList(map), delimiter) + closeStr;
    }

    /**
     * {@link Map}의 항목들을 key=value 형태의 문자열의 컬렉션으로 빌드합니다.
     *
     * @param map Map
     * @return Map 내용을 문자열로 표현한 컬렉션
     */
    @SuppressWarnings("unchecked")
    private static List<String> mapToEntryList(final Map map) {
        List<String> list = new ArrayList<>();
        if (map == null) {
            return list;
        }

        Set<Map.Entry> entrySet = (Set<Map.Entry>) map.entrySet();
        for (Map.Entry entry : entrySet) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }

        return list;
    }
}

