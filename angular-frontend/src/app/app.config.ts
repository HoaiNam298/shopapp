import { ApplicationConfig, importProvidersFrom, Provider } from '@angular/core';
import { provideRouter, RouterModule } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient, withFetch } from '@angular/common/http';
import { TokenIntercepter } from './intercepters/token.intercepter';
import { adminRoutes } from './components/admin/admin-routing.module';

const tokenInterceptorProvider: Provider =
  { provide: HTTP_INTERCEPTORS, useClass: TokenIntercepter, multi: true }

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), 
    importProvidersFrom(RouterModule.forChild(adminRoutes)),
    provideClientHydration(),
    provideHttpClient(withFetch()),
    tokenInterceptorProvider,
    importProvidersFrom(HttpClientModule)
  ]
};
