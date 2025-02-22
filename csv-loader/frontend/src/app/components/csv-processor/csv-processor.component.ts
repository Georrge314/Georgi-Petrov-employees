import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { CsvService, EmployeePairs } from '../../services/csv.service';

@Component({
  selector: 'app-csv-processor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './csv-processor.component.html',
  styleUrl: './csv-processor.component.css',
})
export class CsvProcessorComponent {
  @ViewChild('fileInput') fileInput: ElementRef | undefined;
  public selectedFile: File | null = null;
  public uploadHistory: string[] = [];
  public uploadResult: string = '';
  public resultTable: EmployeePairs[] = [];
  public isDraggingOver: boolean = false;
  public errorMessage: string = '';
  public isLoading: boolean = false;

  constructor(private csvService: CsvService) {}

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onUploadButtonClick(): void {
    if (this.selectedFile) {
      this.uploadFile(this.selectedFile);
    } else {
      console.error('No file selected');
    }
  }

  uploadFile(file: File) {
    this.isLoading = true;
    this.csvService.processFile(file).subscribe({
      next: (response: EmployeePairs[]) => {
        this.addToUploadHistory(file.name);
        this.selectedFile = null;
        if (this.fileInput) {
          this.fileInput.nativeElement.value = '';
        }
        this.resultTable = response;
        this.errorMessage = '';
        this.isLoading = false;
      },
      error: (error) => {
        console.error('File upload failed', error);
        this.errorMessage =
          'File upload failed. Please check the CSV file format and data.';
        this.selectedFile = null;
        if (this.fileInput) {
          this.fileInput.nativeElement.value = '';
        }
        this.isLoading = false;
        this.clearErrorMessageAfterDelay();
      },
    });
  }

  private clearErrorMessageAfterDelay(): void {
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }

  private addToUploadHistory(fileName: string): void {
    this.uploadHistory.unshift(fileName);
    if (this.uploadHistory.length > 10) {
      this.uploadHistory.pop();
    }
  }

  onFileDropped(event: DragEvent): void {
    event.preventDefault();
    const file: File | undefined = event.dataTransfer?.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingOver = false;
  }
}
