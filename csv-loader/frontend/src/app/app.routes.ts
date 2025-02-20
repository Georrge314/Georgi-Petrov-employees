import { Routes } from '@angular/router';
import { CsvProcessorComponent } from './components/csv-processor/csv-processor.component';

export const routes: Routes = [
    { path: 'csv-processor', component: CsvProcessorComponent },
    { path: '', redirectTo: '/csv-processor', pathMatch: 'full' },
    { path: '**', redirectTo: '/csv-processor', pathMatch: 'full' }
];
