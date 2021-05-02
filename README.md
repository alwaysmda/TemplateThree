# TemplateThree
An Android template project that uses `Kotlin` `MVVM` `DataBinding` `OkHttp` `Room` `Kodein` and `BottomNavigationView`.

## Main Features
- Manage Fragments Easily
- Simple and Rich HTTP Request System
- Change Language in Runtime Easily
- Change and Customize Themes
- Easy Shared Element Transition
- Simple Fragments
- Simple RecyclerView Adapters
- Rich Extentions

## ApplicationClass
This is the `Application` class and provides the following features:

### Change Language in Runtime
To change application language, call `changeLang` and pass the `Languages` enum value to the function.
Changing language in different versions of android is a headache. So I created a new system to change language.
Intead of `strings.xml` file, follow the example and add all strings to `ApplicationClass`.
All classes and also layouts have access to `ApplicationClass`.
```
appClass.changeLang(Languages.EN)
```

```
//XML
android:text="@{data.myLocalizedString}"

//CLASS
val myString = appClass.myLocalizedString
```

### Change Theme in Runtime
To change applicatio theme, call `changeTheme` and pass the `Themes` enum value to the function.
Available themes are added in `styles.xml` and you can set different color for anything you want.
Also there is an enum for each theme.
Please note that having `Dark` in theme name changes the application theme to dark mode.
```
appClass.changeTheme(Themes.DARK_BLUE)
```

### Excrypted Shared Preferences
Call `setPref` to save a value and call `getStringPref`, `getBooleanPref`, `getIntPref`, `getLongPref` or `getFloatPref` to get the saved value.
It's best to create the preferences keys in `Constants` to avoid typo mistakes.
```
appClass.setPref(PREF_MY_BOOLEAN,true)
appClass.setPref(PREF_MY_INT,26)
appClass.setPref(PREF_MY_LONG,26L)
appClass.setPref(PREF_MY_FLOAT,2.6F)
appClass.setPref(PREF_MY_STRING,"Twenty Six")

val myBoolean = appClass.getBooleanPref(PREF_MY_BOOLEAN)
val mtInt = appClass.getIntPref(PREF_MY_INT)
val myLong = appClass.getLongPref(PREF_MY_LONG)
val myFloat = appClass.getFloatPref(PREF_MY_FLOAT)
val myString = appClass.getStringPref(PREF_MY_STRING)
```

### Fonts
There are some fonts initialized in `ApplicationClass` and can be accessed anywhere in classes and layouts.
However its best to access fonts using `@font/...` in xmls.
```
//XML
android:fontFamily="@font/font_medium"
//or
android:fontFamily="@{appClass.fontMedium}"

//CLASS
val myTypeface = appClass.fontMedium
```

### Universal RecyclerView Animation
There is a `LayoutAnimationController` instance initialized and can be used as row animations.
```
//XML
android:layoutAnimation="@{appClass.recyclerViewAnimation}"

//CLASS
binding.templateRvTemp.layoutAnimation = appClass.recyclerViewAnimation
```

## BaseActivity
There is only one activity and it is `BaseActivity`.
It controls the fragments and the navigation view.

### Options:
**StartMode**
- SingleInstance: Only one instance of each fragment can be created in each tab.
- MultiInstance: Multiple instances of a fragment can be created.

**ExitMode**
- Normal: After reaching the base fragment in a tab, pressing back button will exit the application.
- BackToFirstTab: After reaching the base fragment in a tab, if it is not the first tab, it returns to fist tab.

**TransitionAnimation**
The animation of switching between fragments.
Possible values are:
- TRANSIT_FRAGMENT_FADE
- TRANSIT_FRAGMENT_OPEN
- TRANSIT_FRAGMENT_CLOSE
- TRANSIT_NONE

### Important Functions:
#### setLoading
Shows a `ProgressBar` and disables touch.
It has an option to disable touch without showing a loading.
```
//Show loading and disable touch
setLoading(true)

//Disable touch without showing a loading
setLoading(true,true)
```

#### start
Starts a new fragment.
You can use `BaseFragmentFactory` to get a fragment instance.
```
start(BaseFragmentFactory.templateFragment())
```

#### selectTab
Navigate between navigation tabs.
There is alse an enum that holds the tab indexes in `BaseActivity`.
```
selectTab(BaseActivity.TAB_ONE)
```

#### initFragmentTable
Call this method and create the initial fragments that will load on each tabs.
It's usually called once, in `onCreate` function of `BaseActivity`.
```
initFragmentTable(
    BaseFragmentFactory.templateFragment(),
    BaseFragmentFactory.templateRoomFragment()
)
```

#### showHideNavigationBar
Shows/Hides the navigation bar with animation.
```
//Show (simple example)
baseActivity.showHideNavigationBar(false)

//Hide (complete example)
baseActivity.showHideNavigationBar(true, 500) {
    //do on hide
}
```

## BaseFragment
All fragments inherit this fragment.
You must call `initialize` in `onAttach` of fragments and pass the layout id and the viewmodel's class.
It has the following variables:
- `appClass` : An instance of `ApplicationClass` that can be used as `Context`
- `viewModel` : An instance of your fragment's ViewModel
- `baseActivity` : An instance of `BaseActivity`
- `binding` : An instance of your `ViewDataBinding` that can be used to access view

### Important Functions:

#### initialize
```
initialize(R.layout.fragment_template, TemplateViewModel::class.java)
```

#### rebind
Refreshes the view's data
```
rebind(data)
```

#### grantPermission
Grant permissions using this method and result will be posted by `OnRequestPermissionResultEvent` event.
```
//Request permissions
grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

//Get results
@Subscribe
fun OnRequestPermissionResultEvent(event: OnRequestPermissionResultEvent) {
  //code here
}
```

#### addSharedElement
Call a event to your fragment and add shared element views right before starting a new fragment.
Views don't need to have `transitionName`.
You only need to add the view you want to animate and the `id` of the target view.
```
addSharedElement(binding.templateIvIconSmall, R.id.template_ivIconLarge)
```

## BaseAdapter:
All adapters inherit this adapter.
This is a generic adapter that holds a list of objects and has the option of showing a loading and an add button.
Unless the `BaseActivity` and `BaseFragment`, this class should be modified based on your project needs.
It has the following variables:
- `showAdd` : Shows an Add button at the beginning
- `showLoading` : Shows a `ProgressBar` as loading at the end
- `sectionIndex` : It is used to differenciate adapters in a nested list
- `addIsLeft` : Whether you want to show the add button on the left or not
- `isHorizontal` : Whether your list is horizontal or not
- `list` : A list of generic objects that bind into rows
- `bindList` : A list of pairs that contain the `position` and the `ViewDataBinding` of rows

In `onCreateViewHolder` the `viewType` variable is the position of the item in list.
`super` must be called for positions below zero, to handle loading and add button.
For the positions of zero and above, `ViewDataBinding` of the row must be created and also `bindList` should be modified just like the example.
Create a `static` view holder class and inherit the `BaseViewHolder` and pass variables of your needs.

## MVVM Workflow
All models extend the `BaseObservable` to notify changes to view as they change.

There are three kinds of data in a viewmodel.
- Local Variables:

Local variables are simple variables that we always use anywhere.

- `SingleLiveEvent` variables:

These are __outgoing__ variables that will be observed in fragments.
This is a one way flow that goes from viewmodel to fragment.
```
//VIEWMODEL
//define
val showMyButton: SingleLiveEvent<Boolean> = SingleLiveEvent()

//use
showMyButton.value = true

//FRAGMENT
//observe
showMyButton.observe(viewLifecycleOwner,{
    it?.let {
        myButton.show()
    }
})
```
- `ObservableField` variables:

These are variables that bind the view and the layouts will notify when the data changes.
It can be also used as two-way binding to get data from user.
```
//VIEWMODEL
//define
val tvTitleText: ObservableField<String> = ObservableField()

//use
tvTitleText.set("My String")


//XML
//one way
android:text="@{viewModel.tvTitleText}"

//two way
android:text="@={viewModel.tvTitleText}"
```

Apart from the three variables above, you can call `public` functions from fragments or layouts.
```
//VIEWMODEL
//define
fun myFunction() {

}

//FRAGMENT
viewModel.myFunction()

//XML
android:onClick="@{() -> viewModel.myFunction()}"
```

## HTTP Requests

__Lets write down the rest later :D__

