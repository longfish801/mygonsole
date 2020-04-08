/*
 * MyGonsole.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.mygonsole

import groovy.ui.*
import groovy.ui.text.*
import groovy.util.logging.Slf4j
import java.awt.*
import javax.swing.*
import javax.swing.text.*
import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * Groovy Consoleをカスタマイズします。<br/>
 * 以下のカスタマイズをしています。</p>
 * <ul>
 * <li>タブを半角スペースに変換しません。</li>
 * <li>システムプロパティ「init.script」が設定されていたら、
 * 初期表示するスクリプトファイルへのパスとみなします。</li>
 * </ul>
 * @version 1.0.00 2020/04/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MyGonsole extends Console {
	/**
	 * コンストラクタ。
	 * @param parent ClassLoader
	 */
	MyGonsole(ClassLoader parent) {
		super(parent)
	}
	
	/**
	 * {@inheritDoc}
	 * GroovyConsoleの代わりにMyGonsoleのインスタンスを生成しています。<br/>
	 * システムプロパティ「init.script」が設定されていたら、
	 * 設定値を初期表示するスクリプトへのパスとみなします。<br/>
	 * --helpオプションの動作は削除しました。
	 */
	static void main(args) {
		// full stack trace should not be logged to the output window - GROOVY-4663
		java.util.logging.Logger.getLogger(StackTraceUtils.STACK_LOG_NAME).useParentHandlers = false

		//when starting via main set the look and feel to system
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		def console = new MyGonsole(Console.class.classLoader?.getRootLoader())
		console.useScriptClassLoaderForScriptExecution = true
		console.run()
		if (args.length == 1){
			LOG.debug('open script: path={}', args[0])
			console.loadScriptFile(args[0] as File)
			return
		}
		if (System.getProperty('init.script') != null){
			LOG.debug('open init script: path={}', System.getProperty('init.script'))
			console.loadScriptFile(System.getProperty('init.script') as File)
			return
		}
	}

	/**
	 * {@inheritDoc}
	 * タブをスペースに置換しないよう修正しました。
	 */
	void run() {
		super.run()
		
		// タブをスペースと解釈しないようTextEditorを設定します
		inputEditor.getTextEditor().isTabsAsSpaces(false)
		
		// GroovyFilterはタブをスペースへ置換してしまうため、代わりにMyGroovyFilterを使用します
		DefaultStyledDocument doc = inputEditor.getTextEditor().getDocument()
		doc.setDocumentFilter(new MyGroovyFilter(doc))
		inputEditor.getTextEditor().setDocument(doc)
		
		// タブの表示サイズを変更します
		setTabSize(inputEditor.getTextEditor() as JTextPane, 4)
	}
	
	/**
	 * 入力したタブをスペースに置換しないようGroovyFilterの動作を改変したクラスです。
	 */
	class MyGroovyFilter extends GroovyFilter {
		/**
		 * コンストラクタ。
		 * @param doc DefaultStyledDocument
		 */
		MyGroovyFilter(DefaultStyledDocument doc){
			super(doc)
		}

		/**
		 * {@inheritDoc}
		 * タブをスペースに置換しないよう修正しました。
		 */
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException {
			fb.insertString(offset, text, attrs)
			parseDocument(offset, text.length())
		}

		/**
		 * {@inheritDoc}
		 * タブをスペースに置換しないよう修正しました。
		 */
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			fb.replace(offset, length, text, attrs)
			parseDocument(offset, text.length())
		}
	}
	
	/**
	 * タブの表示サイズを変更します。
	 * @param jtextPane 変更対象となるJTextPane
	 * @param tabSize タブの表示サイズ
	 */
	static void setTabSize(JTextPane jtextPane, int tabSize){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					FontMetrics fontMetrics = jtextPane.getFontMetrics(jtextPane.getFont())
					int charWidth = fontMetrics.charWidth('m' as char)
					int tabLength = charWidth * tabSize
					int tabsNum = (jtextPane.getWidth() / tabLength) - 1
					TabStop[] tabs = new TabStop[tabsNum]
					for(int j = 0; j < tabs.length; j++) {
						tabs[j] = new TabStop((j + 1) * tabLength)
					}
					TabSet tabSet = new TabSet(tabs)
					SimpleAttributeSet attrs = new SimpleAttributeSet()
					StyleConstants.setTabSet(attrs, tabSet)
					jtextPane.getStyledDocument().setParagraphAttributes(0, jtextPane.getDocument().getLength(), attrs, false)
				} catch (exc){
					exc.printStackTrace()
				}
			}
		})
	}
}
