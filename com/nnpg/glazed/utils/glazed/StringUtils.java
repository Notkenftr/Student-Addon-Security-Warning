package com.nnpg.glazed.utils.glazed;

public class StringUtils {
   public static String convertUnicodeToAscii(String text) {
      StringBuilder result = new StringBuilder();
      char[] var2 = text.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char c = var2[var4];
         switch(c) {
         case 'x':
         case 'Χ':
         case 'χ':
         case 'Х':
         case 'х':
         case 'Ｘ':
         case 'ｘ':
            result.append('x');
            break;
         case 'ǫ':
         case '\ua7af':
         case 'Ｑ':
         case 'ｑ':
            result.append('q');
            break;
         case 'ɢ':
         case 'Ｇ':
         case 'ｇ':
            result.append('g');
            break;
         case 'ɪ':
         case 'Ι':
         case 'І':
         case 'і':
         case 'Ｉ':
         case 'ｉ':
            result.append('i');
            break;
         case 'ɴ':
         case 'Ν':
         case 'η':
         case 'П':
         case 'п':
         case 'Ｎ':
         case 'ｎ':
            result.append('n');
            break;
         case 'ʀ':
         case 'Ｒ':
         case 'ｒ':
            result.append('r');
            break;
         case 'ʏ':
         case 'Υ':
         case 'У':
         case 'у':
         case 'Ｙ':
         case 'ｙ':
            result.append('y');
            break;
         case 'ʙ':
         case 'Β':
         case 'β':
         case 'В':
         case 'в':
         case 'Ｂ':
         case 'ｂ':
            result.append('b');
            break;
         case 'ʜ':
         case 'Η':
         case 'Н':
         case 'н':
         case 'Ｈ':
         case 'ｈ':
            result.append('h');
            break;
         case 'ʟ':
         case 'Ｌ':
         case 'ｌ':
            result.append('l');
            break;
         case 'Α':
         case 'α':
         case 'А':
         case 'а':
         case 'ᴀ':
         case 'Ａ':
         case 'ａ':
            result.append('a');
            break;
         case 'Ε':
         case 'ε':
         case 'Е':
         case 'е':
         case 'ᴇ':
         case 'Ｅ':
         case 'ｅ':
            result.append('e');
            break;
         case 'Ζ':
         case 'ᴢ':
         case 'Ｚ':
         case 'ｚ':
            result.append('z');
            break;
         case 'Κ':
         case 'κ':
         case 'К':
         case 'к':
         case 'ᴋ':
         case 'Ｋ':
         case 'ｋ':
            result.append('k');
            break;
         case 'Μ':
         case 'М':
         case 'м':
         case 'ᴍ':
         case 'Ｍ':
         case 'ｍ':
            result.append('m');
            break;
         case 'Ο':
         case 'ο':
         case 'О':
         case 'о':
         case 'ᴏ':
         case 'Ｏ':
         case 'ｏ':
            result.append('o');
            break;
         case 'Ρ':
         case 'ρ':
         case 'Р':
         case 'р':
         case 'ᴘ':
         case 'Ｐ':
         case 'ｐ':
            result.append('p');
            break;
         case 'Τ':
         case 'τ':
         case 'Т':
         case 'т':
         case 'ᴛ':
         case 'Ｔ':
         case 'ｔ':
            result.append('t');
            break;
         case 'ν':
         case 'ᴠ':
         case 'Ｖ':
         case 'ｖ':
            result.append('v');
            break;
         case 'υ':
         case 'ᴜ':
         case 'Ｕ':
         case 'ｕ':
            result.append('u');
            break;
         case 'ω':
         case 'ᴡ':
         case 'Ｗ':
         case 'ｗ':
            result.append('w');
            break;
         case 'Ѕ':
         case 'ѕ':
         case 'ꜱ':
         case 'Ｓ':
         case 'ｓ':
            result.append('s');
            break;
         case 'Ј':
         case 'ј':
         case 'ᴊ':
         case 'Ｊ':
         case 'ｊ':
            result.append('j');
            break;
         case 'С':
         case 'с':
         case 'ᴄ':
         case 'Ｃ':
         case 'ｃ':
            result.append('c');
            break;
         case 'ᴅ':
         case 'Ｄ':
         case 'ｄ':
            result.append('d');
            break;
         case '″':
            result.append('"');
            break;
         case '　':
            result.append(' ');
            break;
         case 'ꜰ':
         case 'Ｆ':
         case 'ｆ':
            result.append('f');
            break;
         case '！':
            result.append('!');
            break;
         case '＃':
            result.append('#');
            break;
         case '＄':
            result.append('$');
            break;
         case '％':
            result.append('%');
            break;
         case '＆':
            result.append('&');
            break;
         case '＇':
            result.append('\'');
            break;
         case '（':
            result.append('(');
            break;
         case '）':
            result.append(')');
            break;
         case '＊':
            result.append('*');
            break;
         case '＋':
            result.append('+');
            break;
         case '，':
            result.append(',');
            break;
         case '－':
            result.append('-');
            break;
         case '．':
            result.append('.');
            break;
         case '／':
            result.append('/');
            break;
         case '０':
            result.append('0');
            break;
         case '１':
            result.append('1');
            break;
         case '２':
            result.append('2');
            break;
         case '３':
            result.append('3');
            break;
         case '４':
            result.append('4');
            break;
         case '５':
            result.append('5');
            break;
         case '６':
            result.append('6');
            break;
         case '７':
            result.append('7');
            break;
         case '８':
            result.append('8');
            break;
         case '９':
            result.append('9');
            break;
         case '：':
            result.append(':');
            break;
         case '；':
            result.append(';');
            break;
         case '＜':
            result.append('<');
            break;
         case '＝':
            result.append('=');
            break;
         case '＞':
            result.append('>');
            break;
         case '？':
            result.append('?');
            break;
         case '＠':
            result.append('@');
            break;
         case '［':
            result.append('[');
            break;
         case '＼':
            result.append('\\');
            break;
         case '］':
            result.append(']');
            break;
         case '＾':
            result.append('^');
            break;
         case '＿':
            result.append('_');
            break;
         case '｀':
            result.append('`');
            break;
         case '｛':
            result.append('{');
            break;
         case '｜':
            result.append('|');
            break;
         case '｝':
            result.append('}');
            break;
         case '～':
            result.append('~');
            break;
         default:
            result.append(Character.toLowerCase(c));
         }
      }

      return result.toString().toLowerCase();
   }
}
