import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { AuditService } from './audit.service';
import { AuditController } from './audit.controller';

@Module({
  imports: [HttpModule],
  providers: [AuditService],
  controllers: [AuditController],
})
export class AuditModule {}