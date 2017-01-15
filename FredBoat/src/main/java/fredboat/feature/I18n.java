/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fredboat.feature;

import fredboat.db.DatabaseManager;
import fredboat.db.EntityReader;
import fredboat.db.EntityWriter;
import fredboat.db.entities.GuildConfig;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

    private static final Logger log = LoggerFactory.getLogger(I18n.class);

    public static FredBoatLocale DEFAULT = new FredBoatLocale(new Locale("en","US"), "en_US", "English");
    public static final HashMap<String, FredBoatLocale> LANGS = new HashMap<>();

    public static void start() {
        LANGS.put("en_US", DEFAULT);
        LANGS.put("da_DK", new FredBoatLocale(new Locale("da", "DK"), "da_DK", "Dansk"));
        LANGS.put("nl_NL", new FredBoatLocale(new Locale("nl", "NL"), "nl_NL", "Nederlands"));
        LANGS.put("fr_FR", new FredBoatLocale(new Locale("fr", "FR"), "fr_FR", "Français"));
        LANGS.put("de_DE", new FredBoatLocale(new Locale("de", "DE"), "de_DE", "Deutsch"));
        LANGS.put("id_ID", new FredBoatLocale(new Locale("id", "ID"), "id_ID", "Bahasa Indonesia"));
        LANGS.put("it_IT", new FredBoatLocale(new Locale("it", "IT"), "it_IT", "Italiano"));
        LANGS.put("pt_BR", new FredBoatLocale(new Locale("pt", "BR"), "pt_BR", "Português (Brazil)"));
        LANGS.put("ro_RO", new FredBoatLocale(new Locale("ro", "RO"), "ro_RO", "Română"));
        LANGS.put("ru_RU", new FredBoatLocale(new Locale("ru", "RU"), "ru_RU", "Русский"));
        LANGS.put("tr_TR", new FredBoatLocale(new Locale("tr", "TR"), "tr_TR", "Türkçe"));

        LANGS.put("en_TS", new FredBoatLocale(new Locale("en", "TS"), "en_TS", "Tsundere English"));

        log.info("Loaded " + LANGS.size() + " languages: " + LANGS);
    }

    public static ResourceBundle get(Guild guild) {
        if(DatabaseManager.state != DatabaseManager.DatabaseState.READY){
            return DEFAULT.getProps();
        }

        GuildConfig config;

        try {
            config = EntityReader.getGuildConfig(guild.getId());
        } catch (Exception e) {
            log.error("Error when reading entity", e);
            return DEFAULT.getProps();
        }
        return LANGS.getOrDefault(config.getLang(), DEFAULT).getProps();
    }

    public static FredBoatLocale getLocale(Guild guild) {
        if(DatabaseManager.state != DatabaseManager.DatabaseState.READY){
            return DEFAULT;
        }

        GuildConfig config;

        try {
            config = EntityReader.getGuildConfig(guild.getId());
        } catch (Exception e) {
            log.error("Errpr when reading entity", e);
            return DEFAULT;
        }
        return LANGS.getOrDefault(config.getLang(), DEFAULT);
    }

    public static void set(Guild guild, String lang) throws LanguageNotSupportedException {
        GuildConfig config = EntityReader.getGuildConfig(guild.getId());

        if(!LANGS.containsKey(lang))
            throw new LanguageNotSupportedException("Language not found");

        config.setLang(lang);

        EntityWriter.mergeGuildConfig(config);
    }

    public static class FredBoatLocale {

        private final Locale locale;
        private final String code;
        private final ResourceBundle props;
        private final String nativeName;

        FredBoatLocale(Locale locale, String code, String nativeName) throws MissingResourceException {
            this.locale = locale;
            this.code = code;
            props = ResourceBundle.getBundle("lang." + code, locale);
            this.nativeName = nativeName;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getCode() {
            return code;
        }

        public ResourceBundle getProps() {
            return props;
        }

        public String getNativeName() {
            return nativeName;
        }

        @Override
        public String toString() {
            return "[" + nativeName + '}';
        }
    }

    public static class LanguageNotSupportedException extends Exception {

        public LanguageNotSupportedException(String message) {
            super(message);
        }
    }

}