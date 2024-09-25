package com.malviya.demosubscriptionandroidtv.di

import com.malviya.demosubscriptionandroidtv.bindings.decorators.DefaultUIDecorator
import com.malviya.demosubscriptionandroidtv.bindings.decorators.GreenUIDecorator
import com.malviya.demosubscriptionandroidtv.bindings.decorators.RedUIDecorator
import com.malviya.demosubscriptionandroidtv.bindings.decorators.UIDecorator
import org.koin.dsl.module


val uiDecoratorModule = module {
    single<UIDecorator> { DefaultUIDecorator() } // Default Decorator
    factory<UIDecorator> { GreenUIDecorator() }
    factory<UIDecorator> { RedUIDecorator() }
}