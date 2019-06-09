# ExtraInject
Generate (Dagger based) Assisted Injection Factories for ViewModel and ListenableWorker.

## ViewModel
* Annotate ViewModel constructor with `@ViewModelInject` and Extra Parameters with `@Extra`. 
* A Factory will be generated with the ViewModel name suffixed by "Factory". E.g. `MainViewModel` will generate `MainViewModelFactory`.
* Have Dagger inject the Factory and pass the Extras with `factory.with(/*Supply Extras here*/)`
* Finally, pass the Factory to `ViewModelProviders.of()`

## ListenableWorker
* Annotate Worker constructor with `@WorkerInject`.
* The Constructor should have Context and WorkerParameters as first and second Parameters
* A Factory will be generated with the Worker name suffixed by "Factory". E.g. `MainWorker` will generate `MainWorkerFactory`.
* A Dagger Module named `WorkerFactoryModule` will be generated for the WorkerFactory bindings.
* Add DaggerWorkerFactory (from extrainject-work) to the graph. DaggerWorkerFactory will be responsible for creation of Workers using WorkerFactories.

```
    @Singleton
    @Provides
    @JvmStatic
    fun provideWorkerFactory(workerFactoryMap: WorkerFactoryMap): WorkerFactory = DaggerWorkerFactory(workerFactoryMap)
```
Note: WorkerFactoryMap is a Type Alias from extrainject-work.

* And finally, include the generated module into your own Dagger module.

```
@Module(includes = [WorkerFactoryModule::class])
object AppModule {}
```
