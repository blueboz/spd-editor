https://github.com/JetBrains/gradle-intellij-plugin#setup-dsl

https://plugins.jetbrains.com/docs/intellij/plugin-listeners.html#defining-application-level-listeners

https://plugins.jetbrains.com/docs/intellij/plugin-listeners.html


## message Bus usages
project.getMessageBus().connect(Disposable)
.subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, new MyListener())