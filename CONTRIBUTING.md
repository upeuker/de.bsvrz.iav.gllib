Code Convention
===============

- Alle Strings die in Logmeldungen oder Betriebsmeldungen verwendet werden,
  müssen externalisiert werden. Siehe die Java-Klasse FuzzyLibMsg und die Datei
  FuzzyLibMsg.properties. Um dies sicherzustellen gibt es eine entsprechende
  Compiler-Warnung. Alle anderen Strings werden mit "//$NON-NLS-1$"
  gekennzeichnet.
  
- In Unit-Tests müssen keine Strings externalisiert werden. Hier kann die
  entsrepchende Warnung mit der Annotation @SuppressWarnings("nls") an der
  Klasse für alle Strings der Klasse deaktiviert werden.
