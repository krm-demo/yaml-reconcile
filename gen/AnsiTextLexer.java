// Generated from /Users/alekseykurmanov/my/projects/git-hub/yaml-reconcile/src/main/antlr4/org/krmdemo/yaml/reconcile/ansi/AnsiTextLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class AnsiTextLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STYLE_OPEN=1, STYLE_CLOSE=2, CRLF=3, CHAR=4, FG_OPEN=5, BG_OPEN=6, CLOSE_BRACKET=7, 
		HEX_256=8, HEX_RGB=9, CHAR_WS=10, CHAR_COMMA=11, CHAR_SEMICOLON=12, STYLE_CHAR=13;
	public static final int
		STYLE_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "STYLE_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"STYLE_OPEN", "STYLE_CLOSE", "CRLF", "CHAR", "FG_OPEN", "BG_OPEN", "CLOSE_BRACKET", 
			"HEX_256", "HEX_RGB", "HEX_DIGIT", "CHAR_WS", "CHAR_COMMA", "CHAR_SEMICOLON", 
			"STYLE_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@|'", "'|@'", null, null, "'fg('", "'bg('", "')'", null, null, 
			null, "','", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "STYLE_OPEN", "STYLE_CLOSE", "CRLF", "CHAR", "FG_OPEN", "BG_OPEN", 
			"CLOSE_BRACKET", "HEX_256", "HEX_RGB", "CHAR_WS", "CHAR_COMMA", "CHAR_SEMICOLON", 
			"STYLE_CHAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public AnsiTextLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AnsiTextLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\rS\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0003\u0002(\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0003\u0002,\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0000\u0000\u000e\u0002"+
		"\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010"+
		"\b\u0012\t\u0014\u0000\u0016\n\u0018\u000b\u001a\f\u001c\r\u0002\u0000"+
		"\u0001\u0005\u0001\u0000\r\r\u0001\u0000\n\n\u0003\u000009AFaf\u0002\u0000"+
		"\t\t  \u0006\u0000!!(*/9AZ__azR\u0000\u0002\u0001\u0000\u0000\u0000\u0000"+
		"\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000"+
		"\b\u0001\u0000\u0000\u0000\u0001\n\u0001\u0000\u0000\u0000\u0001\f\u0001"+
		"\u0000\u0000\u0000\u0001\u000e\u0001\u0000\u0000\u0000\u0001\u0010\u0001"+
		"\u0000\u0000\u0000\u0001\u0012\u0001\u0000\u0000\u0000\u0001\u0016\u0001"+
		"\u0000\u0000\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0001\u001a\u0001"+
		"\u0000\u0000\u0000\u0001\u001c\u0001\u0000\u0000\u0000\u0002\u001e\u0001"+
		"\u0000\u0000\u0000\u0004#\u0001\u0000\u0000\u0000\u0006+\u0001\u0000\u0000"+
		"\u0000\b-\u0001\u0000\u0000\u0000\n/\u0001\u0000\u0000\u0000\f3\u0001"+
		"\u0000\u0000\u0000\u000e7\u0001\u0000\u0000\u0000\u00109\u0001\u0000\u0000"+
		"\u0000\u0012=\u0001\u0000\u0000\u0000\u0014E\u0001\u0000\u0000\u0000\u0016"+
		"G\u0001\u0000\u0000\u0000\u0018K\u0001\u0000\u0000\u0000\u001aM\u0001"+
		"\u0000\u0000\u0000\u001cQ\u0001\u0000\u0000\u0000\u001e\u001f\u0005@\u0000"+
		"\u0000\u001f \u0005|\u0000\u0000 !\u0001\u0000\u0000\u0000!\"\u0006\u0000"+
		"\u0000\u0000\"\u0003\u0001\u0000\u0000\u0000#$\u0005|\u0000\u0000$%\u0005"+
		"@\u0000\u0000%\u0005\u0001\u0000\u0000\u0000&(\u0007\u0000\u0000\u0000"+
		"\'&\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0001\u0000\u0000"+
		"\u0000),\u0007\u0001\u0000\u0000*,\u0007\u0000\u0000\u0000+\'\u0001\u0000"+
		"\u0000\u0000+*\u0001\u0000\u0000\u0000,\u0007\u0001\u0000\u0000\u0000"+
		"-.\t\u0000\u0000\u0000.\t\u0001\u0000\u0000\u0000/0\u0005f\u0000\u0000"+
		"01\u0005g\u0000\u000012\u0005(\u0000\u00002\u000b\u0001\u0000\u0000\u0000"+
		"34\u0005b\u0000\u000045\u0005g\u0000\u000056\u0005(\u0000\u00006\r\u0001"+
		"\u0000\u0000\u000078\u0005)\u0000\u00008\u000f\u0001\u0000\u0000\u0000"+
		"9:\u0005#\u0000\u0000:;\u0003\u0014\t\u0000;<\u0003\u0014\t\u0000<\u0011"+
		"\u0001\u0000\u0000\u0000=>\u0005#\u0000\u0000>?\u0003\u0014\t\u0000?@"+
		"\u0003\u0014\t\u0000@A\u0003\u0014\t\u0000AB\u0003\u0014\t\u0000BC\u0003"+
		"\u0014\t\u0000CD\u0003\u0014\t\u0000D\u0013\u0001\u0000\u0000\u0000EF"+
		"\u0007\u0002\u0000\u0000F\u0015\u0001\u0000\u0000\u0000GH\u0007\u0003"+
		"\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0006\n\u0001\u0000J\u0017\u0001"+
		"\u0000\u0000\u0000KL\u0005,\u0000\u0000L\u0019\u0001\u0000\u0000\u0000"+
		"MN\u0005;\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0006\f\u0001\u0000"+
		"P\u001b\u0001\u0000\u0000\u0000QR\u0007\u0004\u0000\u0000R\u001d\u0001"+
		"\u0000\u0000\u0000\u0004\u0000\u0001\'+\u0002\u0005\u0001\u0000\u0004"+
		"\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}